package br.com.softdigital.fluig.services;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriterBuilder;
import com.totvs.technology.ecm.foundation.ws.ColleagueDto;
import com.totvs.technology.ecm.foundation.ws.ColleagueDtoArray;
import com.totvs.technology.ecm.foundation.ws.ColleagueService;
import com.totvs.technology.ecm.foundation.ws.ECMColleagueServiceService;
import com.totvs.technology.ecm.foundation.ws.Exception_Exception;
import com.totvs.technology.ecm.foundation.ws.GroupDto;
import com.totvs.technology.ecm.foundation.ws.GroupDtoArray;

import br.com.softdigital.fluig.domains.Credential;

/**
 * Colleague Ws Service
 *
 * @author Pedro Costa
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class ColleagueWsService {

    private final static Logger LOG = Logger.getLogger(ColleagueWsService.class);

    private URL urlWsdl;
    private ColleagueService colleagueService;
    private Credential credentials;

    public ColleagueWsService(String url) throws Exception {
        try {
            colleagueService = getColleagueService(url);
        } catch (MalformedURLException e) {
            LOG.error("Ocorreu uma falha na comunicacao com o Fluig");
            throw new Exception("Ocorreu uma falha na comunicação com o Fluig");
        }
    }

    public ColleagueWsService(Credential credentials) throws Exception {
        try {
            this.credentials = credentials;
            colleagueService = getColleagueService(credentials.getUrl());
        } catch (MalformedURLException e) {
            LOG.error("Ocorreu uma falha na comunicacao com o Fluig \n" + e);
            throw new Exception("Ocorreu uma falha na comunicação com o Fluig");
        }
    }

    private ColleagueService getColleagueService(String url) throws MalformedURLException {
        urlWsdl = new URL(url + "/webdesk/ECMColleagueService?wsdl");

        LOG.info("Obtendo servicos do WSDL (" + urlWsdl + ")");

        ECMColleagueServiceService ecmColleagueService = new ECMColleagueServiceService(urlWsdl);
        return ecmColleagueService.getColleagueServicePort();
    }

    public Credential getCredentials() {
        return credentials;
    }

    public ColleagueDto getColleague(String user, String password) throws Exception {
        LOG.info("Autenticacao e recuperacao de dados do usuario (" + user + ").");

        ColleagueDto colleague = null;

        try {
            colleague = colleagueService.getSimpleColleague(user, password);
        } catch (Exception_Exception e) {
            throw new Exception("Falha ao realizar a autenticação.");
        }

        return colleague;
    }

    public ColleagueDto getColleague() {
        LOG.info("Recuperacao de dados do usuario (" + credentials.getUserName() + ").");

        ColleagueDto colleague = null;

        try {
            colleague = colleagueService.getSimpleColleague(credentials.getUserName(), credentials.getPassword());
        } catch (Exception_Exception e) {
            LOG.error("Ocorreu uma falha ao obter os dados do usuario " + credentials.getUserName() + "\n" + e);
            e.printStackTrace();
        }

        return colleague;
    }

    public List<ColleagueDto> getColleagues() {
        LOG.info("Listando colleagues");

        List<ColleagueDto> listColleague = new ArrayList<ColleagueDto>();

        try {
            ColleagueDtoArray colleagues = colleagueService.getColleagues(
                    credentials.getUserName(),
                    credentials.getPassword(),
                    credentials.getCompany()
            );

            listColleague = colleagues.getItem();
        } catch (Exception_Exception e) {
            LOG.error("Ocorreu uma falha ao obter a lista de usuarios \n" + e);
            e.printStackTrace();
        }

        return listColleague;
    }

    public List<GroupDto> getGroups() {
        LOG.info("Listando grupos");

        List<GroupDto> listGroup = new ArrayList<>();

        try {
            GroupDtoArray groups = colleagueService.getGroups(
                    credentials.getUserName(),
                    credentials.getPassword(),
                    credentials.getCompany(),
                    credentials.getUserCode()
            );
            listGroup = groups.getItem();
        } catch (Exception_Exception e) {
            LOG.error("Ocorreu uma falha ao obter a lista de grupos \n" + e);
            e.printStackTrace();
        }

        return listGroup;
    }

    public void createColleagues(ColleagueDtoArray colleagues) throws Exception_Exception {
        colleagueService.createColleague(
                credentials.getUserName(),
                credentials.getPassword(),
                credentials.getCompany(),
                colleagues
        );
    }

    public void ColleaguesToCsv(File file) throws IOException {
        LOG.info("Criando lista de colleagues em CSV " + file.getPath());

        String[] cabecalho = {"NOME", "EMAIL", "USUARIO", "COD EMPRESA", "LINGUAGEM", "ADMIN", "ATIVO"};
        List<String[]> linhas = new ArrayList<String[]>();

        for (ColleagueDto colleague : getColleagues()) {
            linhas.add(new String[]{
                colleague.getColleagueName(),
                colleague.getMail(),
                colleague.getLogin(),
                String.valueOf(colleague.getCompanyId()),
                colleague.getDefaultLanguage(),
                (colleague.isAdminUser() ? "SIM" : "NAO"),
                (colleague.isActive() ? "SIM" : "NAO")
            });
        }

        Writer writer = Files.newBufferedWriter(file.toPath());
        CSVWriterBuilder csvWriter = new CSVWriterBuilder(writer);

        csvWriter.withSeparator(';');
        csvWriter.build().writeNext(cabecalho);
        csvWriter.build().writeAll(linhas);

        writer.flush();
        writer.close();

        LOG.info("Lista de colleagues em CSV " + file.getPath() + " finalizada.");
    }

    public ColleagueDtoArray csvToColleagues(File file) throws IOException {
        LOG.info("Gerando lista de colleagues a partir do arquivo " + file.getPath());

        Reader reader = Files.newBufferedReader(file.toPath());

        CSVReaderBuilder csvReaderBuilder = new CSVReaderBuilder(reader);
        CSVParserBuilder csvParserBuilder = new CSVParserBuilder();
        csvReaderBuilder.withCSVParser(csvParserBuilder.withSeparator(';').build());
        CSVReader csvReader = csvReaderBuilder.build();

        ColleagueDtoArray colleagues = new ColleagueDtoArray();

        boolean headerControl = true;
        //String[] headerFields = csvReader.peek(); //Recupera a primeira linha do arquivo
        String[] nextRecord;

        while ((nextRecord = csvReader.readNext()) != null) {
            if (headerControl) {
                headerControl = false;
            } else {
                ColleagueDto colleague = new ColleagueDto();

                colleague.setColleagueName(nextRecord[0]);
                colleague.setMail(nextRecord[1]);
                colleague.setLogin(nextRecord[2]);
                colleague.setCompanyId(Long.parseLong(nextRecord[3]));
                colleague.setDefaultLanguage(nextRecord[4]);
                colleague.setAdminUser(nextRecord[5] == "SIM" ? true : false);
                colleague.setActive(nextRecord[6] == "SIM" ? true : false);

                colleagues.getItem().add(colleague);
            }
        }

        LOG.info("Lista de colleagues gerada a partir do arquivo " + file.getPath());
        return colleagues;
    }
}
