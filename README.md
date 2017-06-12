## NestedBird - Sound.je
##### VERSION: RELEASE-CANDIDATE-2
![Travis Tests](https://travis-ci.org/mhaddon/Sound.je.svg?branch=master)

This project contains the code for the website hosted at https://www.sound.je. It is a simple application to help bring together the musical community of Jersey (Channel Islands). This application has been a good learning experience for me in learning more about Java, Spring Boot and ECMAScript 6. Hopefully this project will also be of use to other people.

This website makes use of many technologies built by other wonderful people, please review this page (credits link pending), to see a list of all of the people who deserve credit. *(If your name/project isnt there and should be, please tell me and i will add it).*

I will not likely provide much or any support for anyone wanting to host the server themselves. However, changes made may be merged into the main site at https://www.sound.je.

This is an OpenSource project licensed under GNU Affero General Public License Version 3 (19 November 2007). Please review LICENSE before you use or modify any of the code.

More permissive licensing may be available on request.

#### Documentation
> Java Documentation:         https://mhaddon.github.io/Sound.je/java/index.html

> Javascript Documentation:   https://mhaddon.github.io/Sound.je/js/index.html

> API Documentation:          https://www.sound.je/api

#### Versioning
The live server and this repository may not be completely in sync. The server may have additional patches and changes which will uploaded to the public repository on a regular basis.

#### Getting Started - Prebuilt Virtual Machine
> Please go here: https://mhaddon.github.io/Sound.je.Vagrant/

#### Getting Started - Manual Install
This is just a basic introduction on how to run the server yourself. There wont be too much detail as it is expected you understand the basics.
Please review the gulp modules in ,/gulp to see what tasks there are available.
#### Prerequisites
> **Java 8** (This project uses OpenJDK)

> **A Relational Database** (This project uses MySQL/MariaDB, other databases will require additional configuration).

> **Node & NPM** (If you want to run any of the gulp or npm tasks)

> **Redis**

> **Maven**

#### Installing
> **Clone the repository** *(git clone https://github.com/mhaddon/Sound.je.git)*

> **Navigate to Directory**

> **Install node packages** - run command - *(npm install)*

> **Install gulp globally** - run command -  *(sudo npm install gulp -g)*
```
git clone https://github.com/Sound.je.git
cd Sound.je
npm install
sudo npm install gulp -g
```

#### Using the application
> **Building Javascript changes:** - run command -  *gulp scripts:build*

> **Building SCSS changes:** - run command -  *gulp sass:build*

> **Build Server** - run command - *mvn clean compile package*

> **Run Server** - run command - *java -jar target/NestedBird-1.0.war*

> **Run Server as live:** - run command - *java -envTarget=live -jar target/NestedBird-1.0.war*

> **Connect to server:** - http://localhost:8081

#### Contributing
If anyone wants to contribute to this project than that would be great.
Changes may be pushed to the live site at https://www.sound.je.
I would like it immensely if changes you make follow the same style and syntax as the rest of the project.
There are linting tasks set up as gulp tasks that do this.

#### File Structure:

```
  ├── docs/                                     documentation
  |   ├── js/                                   jsdoc
  |   └── java/                                 javadoc
  |
  ├── gulp/                                     gulp modules
  └── src/main/                                 project source
      |
      ├── webapp/WEB-INF/templates/             html source
      |   |
      |   ├── fragments                         html modules
      |   |   ├── modals                        vue modal components
      |   |   ├── modules                       vue modules
      |   |   └── pages                         vue page components
      |   |
      |   ├── homepage                          homepage layout
      |   └── layout                            general page layout
      |
      ├── resources
      |   ├── build
      |   |   ├── javascript/nestedbird         javascript source
      |   |   |   ├── core                      core classes
      |   |   |   |   ├── Ajax                  module for making AJAX requests
      |   |   |   |   ├── GlobalJS              Global JS overrides (like google analytics)
      |   |   |   |   ├── InfiniteController    Infinite Scrolling Module
      |   |   |   |   ├── KeyController         Key Events Controller Module
      |   |   |   |   ├── musicplayer           Music Player Module
      |   |   |   |   ├── Router                URL Router Module
      |   |   |   |   ├── SchemaReader          Schema Reader Module
      |   |   |   |   └── Util                  Util classes
      |   |   |   ├── Schema                    Normalizr Data Schemas
      |   |   |   ├── showdown                  Showdown modules
      |   |   |   └── Vue                       Custom Vue Code
      |   |   |       ├── components            Vue Components/Modules
      |   |   |       ├── directives            Vue Directives
      |   |   |       ├── mixins                Vue Mixins
      |   |   |       ├── pages                 Vue Pages
      |   |   |       └── store                 VueX Store
      |   |   |
      |   |   └── stylesheet                    sass source using SMACCS folder structure
      |   |
      |   ├── properties                        server properties
      |   ├── static                            public files
      |   └── sql
      |       ├── functions                     sql functions
      |       └── procedures                    sql procedures
      |
      └── java/com.nestedbird                   server source
          ├── components                        server configuration components
          ├── config                            server configuration files
          ├── handlers                          error handlers
          ├── jackson                           jackson parsing objects
          ├── models                            database models
          ├── modules                           extra required modules
          |   |
          |   ├── entitysearch                  searches BaseEntities
          |   ├── facebookreader                scans and reads facebook with the GraphAPI
          |   ├── formparser                    processes BaseEntity form submissions and updates the entities
          |   ├── paginator                     a wrapper for springs pagination, so we can custom paginate lists
          |   ├── permissions                   a permission handler for spring security
          |   ├── ratelimiter                   AOP ratelimiter for the API
          |   ├── resourceparser                creating elements on our site from the data from third party APIs
          |   ├── schema                        reads the schema of model classes
          |   ├── sitemap                       generates a sitemap for a site
          |   ├── soundcloudreader              reads data from sound clouds API
          |   └── youtubereader                 reads data from youtubes API
          |
          ├── util                              utility classes
          └── views                             endpoints to output to the user
```
#### License
```
   NestedBird  Copyright (C) 2016-2017  Michael Haddon

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU Affero General Public License version 3
   as published by the Free Software Foundation.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Affero General Public License for more details.

   You should have received a copy of the GNU Affero General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.
```
More permissive licensing may be available on request.
