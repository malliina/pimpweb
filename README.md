# pimpweb

Repository of the MusicPimp website at www.musicpimp.org.

## Development

Start incremental compilation with hot reload:

    sbt ~build
    
Then navigate to [http://localhost:12345](http://localhost:12345). Code changes in JavaScript or HTML should trigger a 
browser refresh automatically.

## Deployments

The website consists of a main website at [www.musicpimp.org](https://www.musicpimp.org) and a documentation site at
[docs.musicpimp.org](https://docs.musicpimp.org).

To deploy the main website to [www.musicpimp.org](https://www.musicpimp.org):

    sbt release
    
To deploy documentation to [docs.musicpimp.org](https://docs.musicpimp.org)

    mkdocs gh-deploy
