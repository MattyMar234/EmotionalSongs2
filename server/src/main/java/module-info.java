module application.client {
    
    requires java.sql;
    requires org.apache.commons.codec;
    requires transitive javatuples;
    requires java.rmi;
    //requires javax.imageio.spi.RegisterableService;
    
    requires java.desktop; //awt
    requires transitive com.fasterxml.jackson.core;
    requires transitive com.fasterxml.jackson.databind;
    requires transitive com.fasterxml.jackson.annotation;

    requires java.logging;
    requires java.base;
    requires java.naming;
    requires java.management;

    requires jline;
    //requires me.tongfei.progressbar;


  
    exports utility;
    exports interfaces;
    exports objects;
    exports server;
    exports database;
}