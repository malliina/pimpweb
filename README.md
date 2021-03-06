# pimpweb

Repository of the MusicPimp website at [www.musicpimp.org](https://www.musicpimp.org).

## Development

To develop locally, start incremental compilation:

    sbt ~build

Then open any HTML file written to `site`.

### Code structure

- Scala.js frontend code in [client](client)
- Stylesheets in [client/src/main/resources/css](client/src/main/resources/css)
- Site generator in [generator](generator)

## Deployments

The website consists of a main website at [www.musicpimp.org](https://www.musicpimp.org) and a 
documentation site at [docs.musicpimp.org](https://docs.musicpimp.org).

Prerequisites:

- MkDocs:

        pip install mkdocs
        pip install mkdocs-material

### Netlify

Install the Netlify [CLI](https://docs.netlify.com/cli/get-started/):

    npm install netlify-cli -g

Set in build.sbt:

    deployTarget := DeployTarget.Netlify

Deploy via [GitHub Actions](.github/workflows/ci.yml):

    sbt release

To deploy manually:

1. Set your site ID in environment variable NETLIFY_SITE_ID
1. Run:

        sbt deploy

### GitHub Pages

In build.sbt:

    deployTarget := DeployTarget.GitHub("cname.example.com")
    
Run:
    
    sbt release

### GCP

Prerequisites:

- GCP service account credentials in `~/.gcp/credentials.json`.

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
