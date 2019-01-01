# pimpweb

Repository of the MusicPimp website at www.musicpimp.org.

## Deployments

The website consists of a main website at [www.musicpimp.org](https://www.musicpimp.org) and a documentation site at
[docs.musicpimp.org](https://docs.musicpimp.org).

To deploy the main website to [www.musicpimp.org](https://www.musicpimp.org):

    sbt generator/deploy
    
To deploy documentation to [docs.musicpimp.org](https://docs.musicpimp.org)

    mkdocs gh-deploy
