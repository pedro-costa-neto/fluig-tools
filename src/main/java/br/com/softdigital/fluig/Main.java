package br.com.softdigital.fluig;

import br.com.softdigital.fluig.FluigApplication;
import org.apache.log4j.Logger;

public class Main {
    private final static Logger LOG = Logger.getLogger(FluigApplication.class);

    public static void main(String[] args) {
        LOG.info("Iniciando aplicacao");
        FluigApplication.launch(FluigApplication.class);
    }
}
