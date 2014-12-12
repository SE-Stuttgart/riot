# Features entwickeln / Bugs fixen

### Arbeit an neuem Ticket beginnen
```
# JIRA: Ticket zuweisen und auf "In Progress" setzen

# Auf die eigene master-Branch wechseln. Diese sollte mit origin/master übereinstimmen. Also die neuesten Änderungen reinholen:
git fetch origin
git rebase origin/master

# Eine neue Branch für das Ticket erstellen
git checkout -b RIOT-123

# ALTERNATIV kann man die neue Branch basierend auf der Branch von einem vorhergehenden Ticket  (456) erstellen, 
# wenn man diese Änderungen braucht, sie aber noch nicht auf master sind:
git checkout -b RIOT-123 origin/RIOT-456
```

### Arbeit unterbrechen und wieder fortsetzen
Um zwischenzeitlich an einem anderen Ticket weiterzuarbeiten oder seinen Zwischenstand hochladen zu können oder den master-Stand anzuschauen,
muss man die Arbeit am aktuellen Ticket unterbrechen. Das tut man, indem man einfach temporär committet.

```
git add .
git status
# Prüfen dass git status alles auf "Changes to be committed" hat.

# Bitte hier nicht die exakte Ticketnummer RIOT-123 einbauen, weil sonst JIRA denkt, das Ticket wäre fertig:
git commit -m "TEMP-123: Zwischenstand Beschreibung"
# Jetzt kann man auf andere Branches wechseln oder diese Branch pushen.
```

Um die Arbeit dann später wieder fortzusetzen:

```
# Zur Branch zurückkehren:
git checkout RIOT-123
# Den temporären Commit aufbrechen:
git reset --soft HEAD^
git reset .
# Weiterarbeiten.
```

### Den aktuellen Stand von master reinholen
Um ein Ticket weiter zu bearbeiten, das Änderungen von anderen Entwicklern benötigt, die zwischenzeitlich gemacht wurden,
muss man diese "unter" die eigenen Änderungen packen.

```
# Eigene Änderungen weg-stashen:
git stash

# Die neuen Änderungen holen:
git fetch origin
git rebase origin/master

# Die eigenen Änderungen wieder zurückholen:
git stash pop

# Dabei kann es natürlich zu Merge-Konflikten kommen, die dann als "Modified by both" oder so in "git status" in roter 
# Farbe angezeigt werden. Diese behebt man und markiert sie mit "git add <datei>" als gelöst.
```

### Arbeit an einem Ticket abschließen
```
# Änderungen nochmal anschauen, wenn es viele waren oder sie lange her waren.
# Dazu kann man eine Git-UI wie SourceTree oder den GitHub-Client oder so verwenden, oder:
git diff

# Änderungen committen:
git add .
# Es wären auch selektive Commits mit "git add <datei>" möglich.
git commit -m "RIOT-123: Commit-Meldung"

# Änderungen auf aktuellen Stand rebasen:
git fetch origin
git rebase origin/master
# Wenn Rebase-Konflikte auftreten, diese beheben (siehe unten).

# Änderungen hochladen:
git push origin RIOT-123
# Wenn Git beim Pushen einen non-fast-forward meldet, stellt man sicher, 
# dass das Wort "master" im eingegebenen Befehl nicht vorkommt und verwendet "git push --force origin RIOT-123".

# JIRA auf "In Review" setzen.
# Auf die master-Branch zurückkehren
git checkout master
git rebase origin/master
```

### Nachträglich Merge-Konflikte bei eigenem Ticket beheben
Wenn ein Reviewer einen Merge-Konflikt feststellt, sollte man ihn als Entwickler selbst beheben.
Man hat natürlich die Branch noch, d.h. man aktualisiert sie und pusht sie dann neu.

```
# Alte Branch wieder öffnen:
git checkout RIOT-123

# Rebasen:
git fetch origin
git rebase origin/master

# Hier dürften jetzt Merge-Konflikte auftreten. Diese muss man beheben (siehe unten).
# Nach dem beheben ist "git status" wieder leer und "git log" zeigt den eigenen Commit linear über denen von master an.

# Hochladen:
git push --force origin RIOT-123
# In JIRA wieder auf "In Review" stellen.
```

### Nachträglich Änderungen einbauen
Wenn einem Reviewer etwas aufgefallen ist, was geändert werden muss, kehrt man zur alten Branch zurück und ändert es dort.

```
# Alte Branch wieder öffnen:
git checkout RIOT-123

# Rebasen (nur zur Sicherheit, weil es ja eine alte Branch ist):
git fetch origin
git rebase origin/master

# Wenn Merge-Konflikte auftreten, muss man sie beheben (siehe oben).

# Alten Commit aufbrechen
git reset --soft HEAD^

# Die bisherigen Änderungen müssten jetzt in "git status" drin sein.
# Ab jetzt kann man wie beim Implementieren eines neuen Tickets weiterarbeiten.
```

# Rebasen
Wenn man einen rebase-Befehl ausführt und dabei Konflikte auftreten, kann man mit "git status" sehen, welche Dateien betroffen sind.
In diesen Dateien muss man das Problem dann beheben (also z.B. in Eclipse). Anschließend markiert man es mit "git add <datei>" als gelöst.
Wenn die Probleme in allen Dateien aufgelöst sind, schließt man mit "git rebase --continue" ab.
Wenn man die Probleme nicht beheben möchte, bricht man mit "git rebase --abort" ab.

# Reviews
### Ein Ticket reviewen
```
# Die Branch herunterladen und auschecken:
git fetch origin
git checkout -b integration123 origin/RIOT-123

# Rebasen und auf Merge-Konflikte prüfen:
git rebase origin/master
# Wenn hier Merge-Konflikte auftreten, bricht man ab (git rebase --abort), gibt in JIRA Bescheid,
# dass Merge-Konflikte zu beheben sind, und stellt es wieder auf "In Progress".

# Den Code anschauen.

# Wenn OK:
# Integrieren und das JIRA-Ticket auf "Done" stellen.
git push origin integration123:master
git checkout master
git rebase origin/master
git branch -D integration123
git push origin :RIOT-123

# Wenn NOK:
# Im Ticket oder in Gitlab Kommentare hinterlassen, was zu verbessern ist oder was aufgefallen ist.
# Die Branch wegwerfen
git checkout master
git branch -D integration123
```