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

Deploy via [GitHub Actions](.github/workflows/ci.yml):

    sbt release

To deploy manually:

1. Set your site ID in environment variable NETLIFY_SITE_ID
2. Run:

        sbt deploy

## Implementation Notes

The documentation site is a GitHub Pages site using the 
[Material theme](https://squidfunk.github.io/mkdocs-material/) for 
[MkDocs](https://www.mkdocs.org/).
