# MessengerFX

Simple messenger that works with several social networks using API's and JavaFX.

Project in development.

- [x] Interface using JavaFX
- [x] VK authorization
- [x] VK Friend list
- [x] Send and receive messages with VK API
- [x] Skype Contact list
- [x] Send and receive messages with Skype Web API
- [ ] Atachment info
- [ ] Facebook authorization
- [ ] Telegram authorization


**Build using Java 11/12**

VM options:
> -Djava.util.logging.config.file=logging.properties 

*Don't forget to change the path to JavaFX SDK*
> --module-path /path/to/sdk/javafx-sdk-12.0.1/lib/

> --add-modules javafx.controls,javafx.web,javafx.fxml 

> --add-exports javafx.base/com.sun.javafx.logging=ALL-UNNAMED 

> --add-exports javafx.graphics/com.sun.glass.ui=ALL-UNNAMED 

> --add-exports javafx.graphics/com.sun.glass.utils=ALL-UNNAMED 

> --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED 

> --add-exports javafx.graphics/com.sun.javafx.font=ALL-UNNAMED 

> --add-exports javafx.graphics/com.sun.javafx.geom=ALL-UNNAMED 

> --add-exports javafx.graphics/com.sun.javafx.geom.transform=ALL-UNNAMED 

> --add-exports javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED 

> --add-exports javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED 

> --add-exports javafx.graphics/com.sun.javafx.scene.input=ALL-UNNAMED 

> --add-exports javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED 

> --add-exports javafx.graphics/com.sun.javafx.text=ALL-UNNAMED 

> --add-exports javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED 

> --add-exports javafx.graphics/com.sun.javafx.util=ALL-UNNAMED 

> --add-exports javafx.graphics/com.sun.prism=ALL-UNNAMED 

> --add-exports javafx.graphics/com.sun.prism.paint=ALL-UNNAMED 

> --add-exports javafx.graphics/com.sun.scenario.effect=ALL-UNNAMED
