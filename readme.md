# Qiet

Utilisation de mapbox pour l'affichage de la map, pour sa documentation complète et la possibilité d'envoyer jusqu'à 150000 requêtes/mois, idéal 
si l'on souhaite tester sans payer avec beaucoup de possibilités de customisation. La documentation est claire et agréable à parcourrir contrairement aux docs google
parfois pas à jour et trop compliquées à prendre en main.

Choix du design patern : MVP, pattern efficace et modulaire qui se prête parfaitement à l'implémentation d'un poc.
Il offre une modularité, une testabilité et une base de code  propre et  facile à maintenir

Création d'un singleton "QietSingleton" prenant en charge qui permet de getter/setter les propriétés des onjets Alarm et User tout au long du cycle de vie de l'application
depuis n'importe quelle classe du projet.

Pour la partie réseau on aurait pu créer une interface pour déclarer nos services avec un datamanager qui l'implemente avec un retrofit couplé à du rxkotlin pour la gestion
des appels et du système de callback.

On aurait pu ajouter deux champs qui permet de setter directement les coordoonées de l'adresse du lieu à proteger.

Scénarios de test :

Si le mode de gestion de l'alarme est en "auto" et que  Bamby s'eloigne à plus de 20 mètres de l'alarme, l'alarme s'active (VERT)
Si le mode de gestion de l'alarme est en "manuel" et que Bamby active l'alarme manuellement, l'alarme est activée (VERT)
Si le mode de gestion de l'alarme est en "manuel" et que Bamby désactive l'alarme manuellement, l'alarme est desactivée (ROUGE)


Si le mode de gestion de l'alarme est en "auto" et que Alexis s'eloigne à plus de 20 mètres de l'alarme, l'alarme reste inactivée puisque Alexis n'est pas le
dernier à quitter les locaux.(ROUGE) 
Si le mode de gestion de l'alarme est en "manuel" et que Alexis active l'alarme manuellement, l'alarme est activée (VERT)
Si le mode de gestion de l'alarme est en "manuel" et que Alexis désactive l'alarme manuellement, l'alarme est desactivée (ROUGE)


Pour tester l'app, avec Bamby :
login : bamby
pwd : qiet

Pour tester l'app, avec Alexis :
login : alexis
pwd : qiet




