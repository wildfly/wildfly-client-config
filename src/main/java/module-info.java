module org.wildfly.client.config {
    requires java.xml;

    requires org.jboss.logging;
    requires static org.jboss.logging.annotations;

    requires org.wildfly.common;

    exports org.wildfly.client.config;

    uses org.wildfly.client.config.ResolverProvider;
}