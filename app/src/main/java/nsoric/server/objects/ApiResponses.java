package nsoric.server.objects;

import java.io.IOException;
import java.util.List;

/**
 * Trieda obsahujuca metody get, kde kazda posiela na server konkretnu poziadavku a vzapati vracia opdoved serveru na tuto poziadavku.
 */
class ApiResponses {



    private ClientSocket clientSocket;

    /**
     * Konstruktor triedy ApiResponses, inicializuje objekt triedy ClientSocket a zaroven posle
     * udaje jej konstruktoru, na zaklade ktorych sa vytvori spojenie.
     */
    ApiResponses(String login, String pass, String server, int port, String client) {
        clientSocket = new ClientSocket(login, pass, server, port, client);
    }


    /**
     * Vracia odpoved vo formate String s datami z entity {@link database.objects.db.entity.User}.
     *
     * @return (String) odpoved serveru na poziadavku
     * @throws IOException v pripade chyby pri komunikacii s API
     */
    String getUserResponse() throws IOException {
        return clientSocket.sendRequest("data", "get", "users", null);
    }

    /**
     * Vracia odpoved vo formate String s datami z entity {@link database.objects.db.entity.Sensor}.
     *
     * @return (String) odpoved serveru na poziadavku
     * @throws IOException v pripade chyby pri komunikacii s API
     */
    String getSensorResponse() throws IOException {
        return clientSocket.sendRequest("data", "get", "sensor", null);
    }

    /**
     * Vracia odpoved vo formate String s datami z entity {@link database.objects.db.entity.Sector}.
     *
     * @return (String) odpoved serveru na poziadavku
     * @throws IOException v pripade chyby pri komunikacii s API
     */
    String getSectorResponse() throws IOException {
        return clientSocket.sendRequest("data", "get", "sector", null);
    }

    /**
     * Vracia odpoved vo formate String s datami z entity {@link database.objects.db.entity.Area}.
     *
     * @return (String) odpoved serveru na poziadavku
     */
    String getAreaResponse() throws IOException {
        return clientSocket.sendRequest("data", "get", "area", null);
    }

    /**
     * Vracia odpoved vo formate String s datami z entity {@link database.objects.db.entity.SensorSector}.
     *
     * @return (String) odpoved serveru na poziadavku
     * @throws IOException v pripade chyby pri komunikacii s API
     */
    String getSensorSectorResponse() throws IOException {
        return clientSocket.sendRequest("data", "get", "sensor_sector", null);
    }

    /**
     * Vracia odpoved vo formate String s datami z entity {@link database.objects.db.entity.Measurement}.
     *
     * @return (String) odpoved serveru na poziadavku
     * @throws IOException v pripade chyby pri komunikacii s API
     */
    String getMeasurementResponse() throws IOException {
        return clientSocket.sendRequest("data", "get", "measurement", null);
    }

    /**
     * Vracia odpoved vo formate String s datami z entity {@link database.objects.db.entity.SensorProperties}.
     *
     * @return (String) odpoved serveru na poziadavku
     * @throws IOException v pripade chyby pri komunikacii s API
     */
    String getSensorPropertiesResponse() throws IOException {
        return clientSocket.sendRequest("data", "get", "sensor_properties", null);
    }

    /**
     * Vracia odpoved vo formate String s datami z entity {@link database.objects.db.entity.GroupView}.
     *
     * @return (String) odpoved serveru na poziadavku
     * @throws IOException v pripade chyby pri komunikacii s API
     */
    String getGroupViewResponse() throws IOException {
        return clientSocket.sendRequest("data", "get", "group_view", null);
    }


    /**
     * Vracia odpoved vo formate String s datami z entity {@link database.objects.db.entity.Type}.
     *
     * @return (String) odpoved serveru na poziadavku
     * @throws IOException v pripade chyby pri komunikacii s API
     */
    String getTypeResponse() throws IOException {
        return clientSocket.sendRequest("data", "get", "type", null);
    }

    /**
     * Vracia odpoved vo formate String s datami z entity {@link database.objects.db.entity.TypeGroup}.
     *
     * @return (String) odpoved serveru na poziadavku
     * @throws IOException v pripade chyby pri komunikacii s API
     */
    String getTypeGroupResponse() throws IOException {
        return clientSocket.sendRequest("data", "get", "type_group1", null);
    }

    /**
     * Vracia odpoved vo formate String s nameranymi hodnotami, entity {@link database.objects.db.entity.SensorDate} a {@link database.objects.db.entity.SensorValue}.
     *
     * @param dates List datumov vo formate String od- do
     * @return (String) odpoved serveru na poziadavku
     * @throws IOException v pripade chyby pri komunikacii s API
     */
    String getSensorValuesResponse(List<String> dates) throws IOException {
        return clientSocket.sendRequest("sensor", "get", "", dates);
    }


}
