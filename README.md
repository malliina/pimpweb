# pimpweb

Repository of the MusicPimp website at [www.musicpimp.org](https://www.musicpimp.org).

## Development

To develop locally, start incremental compilation:

    sbt ~build
    
Then navigate to [http://localhost:12345](http://localhost:12345).

Code changes in JavaScript, HTML or CSS triggers a browser refresh automatically.

### Code structure

- Scala.js frontend code in [client](client)
- Stylesheets in [client/src/main/resources/css](client/src/main/resources/css)
- Site generator in [generator](generator)

## Deployments

The website consists of a main website at [www.musicpimp.org](https://www.musicpimp.org) and a 
documentation site at [docs.musicpimp.org](https://docs.musicpimp.org).

Prerequisites:

1. GCP service account credentials in `~/.gcp/credentials.json`.
1. MkDocs:

        pip install mkdocs
        pip install mkdocs-material

To deploy both the main website and documentation site:

    sbt release

## Implementation Notes

This is a static website with HTML and other frontend assets generated at build time. On deployment, 
generated files are uploaded to a bucket named www.musicpimp.org in Google Cloud Storage and served 
via a CNAME entry in CloudFlare.

The documentation site is a GitHub Pages site using the 
[Material theme](https://squidfunk.github.io/mkdocs-material/) for 
[MkDocs](https://www.mkdocs.org/).

### HTML generation

Implement trait [Generator](generator/src/main/scala/com/malliina/generator/Generator.scala). It has one method:

    def pages(assets: MappedAssets, assetFinder: AssetFinder, mode: AppMode): BuiltPages
    
Its return value is a mapping of HTML content to paths, which is used by the site generator to deploy the desired pages.

### Asset generation

I use [scalajs-bundler](https://scalacenter.github.io/scalajs-bundler/) to control webpack. The 
order in which JavaScript files are defined in a website may matter, therefore the bundled JS assets 
are delivered to the site generator in the same order they are expected to be defined in the HTML.

Assets are also:

- fingerprinted at build time for aggressive caching
- gzip-compressed at deploy time

### Hot-reloading

I use 

    "com.lihaoyi" % "workbench" % "0.4.1"
    
instead of webpack-dev-server for hot reloading, because I also want to trigger the site generation
on Scala code changes whereas by default webpack-dev-server would only build frontend assets.

The workbench plugin also prints compilation output to the browser console, which is nice.

### Hot-reloading CSS

CSS files are hot-reloaded. For reference, it is setup as follows:

1. Put your CSS files under a `resourceDirectory` of the frontend project, 
for example [client/src/main/resources/css](client/src/main/resources/css).

1. In the Scala.js project, make sure webpack picks up changes to CSS files:

        webpackMonitoredDirectories ++= (resourceDirectories in Compile).value.map { dir =>
            dir / "css"
        },
        includeFilter in webpackMonitoredFiles := "*.less"
        
1. The SBT *build* task launches webpack when sources change, so make sure the CSS sources are also
watched by the build:

        watchSources ++= (resourceDirectories in Compile).value.map { dir =>
            WatchSource(dir / "css", "*.less", HiddenFileFilter)
        }
