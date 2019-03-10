# pimpweb

Repository of the MusicPimp website at [www.musicpimp.org](https://www.musicpimp.org).

## Development

To develop locally, start incremental compilation:

    sbt ~build
    
Then navigate to [http://localhost:12345](http://localhost:12345).

Code changes in JavaScript or HTML should trigger a browser refresh automatically.

## Deployments

The website consists of a main website at [www.musicpimp.org](https://www.musicpimp.org) and a documentation site at
[docs.musicpimp.org](https://docs.musicpimp.org).

To deploy both the main website and documentation site:

    sbt release

## Implementation Notes

This is a static website with HTML and other frontend assets generated at build time. On deployment, generated files are 
uploaded to a bucket named www.musicpimp.org in Google Cloud Storage and served via a CNAME entry in CloudFlare.

The documentation site is a GitHub Pages site using the [Material theme](https://squidfunk.github.io/mkdocs-material/) 
for [MkDocs](https://www.mkdocs.org/).
