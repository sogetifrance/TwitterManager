TwitterManager

Application appEngine permettant de :
- envoyer un même message direct à tous ses folowers
- entretenir sa liste d'amis en fonction d'une configuration par mots clés.
- modifier la configuration par mots clés.

Usage (After changing the app id in the twitter-manager-ear/src/main/application/META-INF/appengine-application.xml):

    git clone https://github.com/sogetifrance/TwitterManager
    cd TwitterManager
    mvn install
    cd twitter-manager-ear
    #to test it locally:
    mvn appengine:devserver
    #or to deploy it on google app engine:
    mvn appengine:update

=============================
