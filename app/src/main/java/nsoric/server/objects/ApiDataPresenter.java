package nsoric.server.objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import database.objects.db.entity.Area;
import database.objects.db.entity.GroupView;
import database.objects.db.entity.Measurement;
import database.objects.db.entity.Sector;
import database.objects.db.entity.Sensor;
import database.objects.db.entity.SensorDate;
import database.objects.db.entity.SensorProperties;
import database.objects.db.entity.SensorSector;
import database.objects.db.entity.SensorValue;
import database.objects.db.entity.Type;
import database.objects.db.entity.TypeGroup;
import database.objects.db.entity.User;

/**
 * Trieda ktora zabezpecuje parsovanie odpovedi, casti "data" z API.
 */
public class ApiDataPresenter {
    private static final String DATA = "data";

    private ArrayList<Sensor> sensorList;
    private ArrayList<Sector> sectorList;
    private ArrayList<SensorSector> sensorSectorList;
    private ArrayList<Area> areaList;
    private ArrayList<User> userList;
    private ArrayList<GroupView> groupViewList;
    private ArrayList<SensorDate> sensorDateList;
    private ArrayList<Measurement> measurementList;
    private ArrayList<SensorValue> sensorValueList;
    private ArrayList<SensorProperties> sensorPropertiesList;
    private ArrayList<Type> typeList;
    private ArrayList<TypeGroup> typeGroupList;

    private ApiResponses apiResponses;

    /**
     * Konstruktor vytvori objekt triedy {@link ApiResponses}, cez ktory sa bude pristupovat k jednotlivym odpovediam API.
     *
     * @param login  prihl. meno
     * @param pass   prihl. heslo
     * @param server adresa serveru
     * @param port   port
     * @param client dodatocna info
     */
    public ApiDataPresenter(String login, String pass, String server, int port, String client) {
        this.apiResponses = new ApiResponses(login, pass, server, port, client);
    }

    /**
     * Rozparsovanie JSON odpovedi z APi pre entitu {@link User} na serveri.
     *
     * @throws JSONException
     * @throws IOException
     */
    private void parseUserResponse() throws JSONException, IOException {
        this.userList = new ArrayList<>();
        String serverResponse = apiResponses.getUserResponse();
        if (!serverResponse.isEmpty()) {
            JSONObject response = new JSONObject(serverResponse);
            //vytvori JSON array z casti kde sa JSON "key"=>"data"
            JSONArray array = response.getJSONArray(DATA);
            User user;
            for (int i = 0; i < array.length(); i++) {
                user = null;
                user = new User();
                user.setId(array.getJSONArray(i).getInt(0));
                user.setLogin(array.getJSONArray(i).getString(1));
                user.setPassword(array.getJSONArray(i).getString(2));
                user.setName(array.getJSONArray(i).getString(3));
                user.setGroupID(array.getJSONArray(i).getInt(4));
                this.userList.add(user);
            }
        }
    }

    /**
     * Rozparsovanie JSON odpovedi z APi pre entitu {@link Sensor} na serveri.
     *
     * @throws JSONException
     * @throws IOException
     */
    private void parseSensorResponse() throws JSONException, IOException {
        this.sensorList = new ArrayList<>();
        String serverResponse = apiResponses.getSensorResponse();
        if (!serverResponse.isEmpty()) {
            JSONObject response = new JSONObject(serverResponse);
            JSONArray array = response.getJSONArray(DATA);
            Sensor sensor;
            for (int i = 0; i < array.length(); i++) {
                sensor = null;
                sensor = new Sensor();
                sensor.setId(array.getJSONArray(i).getInt(0));
                sensor.setName(array.getJSONArray(i).getString(1));
                sensor.setUid(array.getJSONArray(i).getString(2));
                sensor.setTypeID(array.getJSONArray(i).getInt(3));
                sensor.setLevel(array.getJSONArray(i).getInt(4));
                sensor.setMinValue(array.getJSONArray(i).getInt(5));
                sensor.setMaxValue(array.getJSONArray(i).getInt(6));
                sensor.setBroadcastGroup(array.getJSONArray(i).getInt(7));
                sensor.setStorageClass(array.getJSONArray(i).getInt(8));
                this.sensorList.add(sensor);
            }
        }
    }

    /**
     * Rozparsovanie JSON odpovedi z APi pre entitu {@link Sector} na serveri.
     *
     * @throws JSONException
     * @throws IOException
     */
    private void parseSectorResponse() throws JSONException, IOException {
        this.sectorList = new ArrayList<>();
        String serverResponse = apiResponses.getSectorResponse();
        if (!serverResponse.isEmpty()) {
            JSONObject response = new JSONObject(serverResponse);
            JSONArray array = response.getJSONArray(DATA);
            Sector sector;
            for (int i = 0; i < array.length(); i++) {
                sector = null;
                sector = new Sector();
                sector.setId(array.getJSONArray(i).getInt(0));
                sector.setName(array.getJSONArray(i).getString(1));
                sector.setAreaID(array.getJSONArray(i).getInt(2));
                this.sectorList.add(sector);
            }
        }
    }

    /**
     * Rozparsovanie JSON odpovedi z APi pre entitu {@link Area} na serveri.
     *
     * @throws JSONException
     * @throws IOException
     */
    private void parseAreaResponse() throws JSONException, IOException {
        this.areaList = new ArrayList<>();
        String serverResponse = apiResponses.getAreaResponse();
        if (!serverResponse.isEmpty()) {
            JSONObject response = new JSONObject(serverResponse);
            JSONArray array = response.getJSONArray(DATA);
            Area area;
            for (int i = 0; i < array.length(); i++) {
                area = null;
                area = new Area();
                area.setId(array.getJSONArray(i).getInt(0));
                area.setName(array.getJSONArray(i).getString(1));
                this.areaList.add(area);
            }
        }
    }


    /**
     * Rozparsovanie JSON odpovedi z APi pre entitu {@link SensorSector}.
     *
     * @throws JSONException
     * @throws IOException
     */
    private void parseSensorSectorResponse() throws JSONException, IOException {
        this.sensorSectorList = new ArrayList<>();
        String serverResponse = apiResponses.getSensorSectorResponse();
        if (!serverResponse.isEmpty()) {
            JSONObject response = new JSONObject(serverResponse);
            JSONArray array = response.getJSONArray(DATA);
            SensorSector sSector;
            for (int i = 0; i < array.length(); i++) {
                sSector = null;
                sSector = new SensorSector();
                sSector.setId(array.getJSONArray(i).getInt(0));
                sSector.setSensorID(array.getJSONArray(i).getInt(1));
                sSector.setSectorID(array.getJSONArray(i).getInt(2));
                this.sensorSectorList.add(sSector);
            }
        }
    }

    /**
     * Rozparsovanie JSON odpovedi z APi pre entitu {@link Measurement}.
     *
     * @throws JSONException
     * @throws IOException
     */
    private void parseMeasurementResponse() throws JSONException, IOException {
        this.measurementList = new ArrayList<>();
        String serverResponse = apiResponses.getMeasurementResponse();
        if (!serverResponse.isEmpty()) {
            JSONObject response = new JSONObject(serverResponse);
            JSONArray array = response.getJSONArray(DATA);
            Measurement measurement;
            for (int i = 0; i < array.length(); i++) {
                measurement = null;
                measurement = new Measurement();
                measurement.setId(array.getJSONArray(i).getInt(0));
                measurement.setName(array.getJSONArray(i).getString(1));
                measurement.setMeasurementType(array.getJSONArray(i).getInt(2));
                measurement.setSid(array.getJSONArray(i).getInt(3));
                this.measurementList.add(measurement);
            }
        }
    }

    /**
     * Rozparsovanie JSON odpovedi z APi pre entitu {@link SensorProperties}.
     *
     * @throws JSONException
     * @throws IOException
     */
    private void parseSensorPropertiesResponse() throws JSONException, IOException {
        this.sensorPropertiesList = new ArrayList<>();
        String serverResponse = apiResponses.getSensorPropertiesResponse();
        if (!serverResponse.isEmpty()) {
            JSONObject response = new JSONObject(serverResponse);
            JSONArray array = response.getJSONArray(DATA);
            String level_lo;
            String level_hi;
            SensorProperties sProperties;
            for (int i = 0; i < array.length(); i++) {
                sProperties = null;
                sProperties = new SensorProperties();
                sProperties.setId(array.getJSONArray(i).getInt(0));
                sProperties.setSensorID(array.getJSONArray(i).getInt(1));
                sProperties.setSensorOrder(array.getJSONArray(i).getInt(2));
                level_lo = array.getJSONArray(i).getString(3);
                level_hi = array.getJSONArray(i).getString(4);
                if (level_lo.equals("null")) {
                    sProperties.setLevelLowest(Double.MIN_VALUE);
                } else {
                    sProperties.setLevelLowest(Double.valueOf(level_lo));
                }
                if (level_hi.equals("null")) {
                    sProperties.setLevelHighest(Double.MAX_VALUE);
                } else {
                    sProperties.setLevelHighest(Double.valueOf(level_hi));
                }
                this.sensorPropertiesList.add(sProperties);
            }
        }
    }

    /**
     * Rozparsovanie JSON odpovedi z APi pre entitu {@link GroupView}.
     *
     * @throws JSONException
     * @throws IOException
     */
    private void parseGroupViewResponse() throws JSONException, IOException {
        this.groupViewList = new ArrayList<>();
        String serverResponse = apiResponses.getGroupViewResponse();
        if (!serverResponse.isEmpty()) {
            JSONObject response = new JSONObject(serverResponse);
            JSONArray array = response.getJSONArray(DATA);
            GroupView gView;
            for (int i = 0; i < array.length(); i++) {
                gView = null;
                gView = new GroupView();
                gView.setId(array.getJSONArray(i).getInt(0));
                gView.setAreaID(array.getJSONArray(i).getInt(1));
                gView.setUserID(array.getJSONArray(i).getInt(2));
                this.groupViewList.add(gView);
            }
        }
    }

    /**
     * Rozparsovanie JSON odpovedi z APi pre entity {@link SensorDate} a {@link SensorValue}.
     *
     * @param dates List datumov vo formate String od- do, pre ktore sa maju merani stiahnut
     * @throws JSONException
     * @throws IOException
     */
    private void parseSensorDatesAndValuesResponse(List<String> dates) throws JSONException, IOException {
        this.sensorValueList = new ArrayList<>();
        this.sensorDateList = new ArrayList<>();
        String serverResponse = apiResponses.getSensorValuesResponse(dates);
        if (serverResponse != null && !serverResponse.isEmpty()) {
            JSONObject response = new JSONObject(serverResponse);
            JSONArray array = response.getJSONArray(DATA);
            //dateKey su hodnoty kluca JSON objektu, tj. jednotlive datumy a casy merani
            List<String> dateKey = new ArrayList<>();
            //measurementKey su hodnoty kluca JSON objektu, idcka merani
            List<String> measurementKey = new ArrayList<>();
            //Json objekt este s datumami
            JSONObject joDate;
            //Json objekt len hodnota ku klucu-datum
            JSONObject joDataForDate;
            //Json array vnutorna hodnota k jednemu meraniu s measurementKey
            JSONArray arrayWithData;
            int dateID = 0;
            int i, j = 0;
            SensorDate sensorDate;
            SensorValue sensorValue;
            for (i = 0; i < array.length(); i++) {
                joDate = array.getJSONObject(i);
                if (joDate.keys().hasNext()) {
                    dateKey.add(joDate.keys().next());
                    joDataForDate = joDate.getJSONObject(dateKey.get(i));

                    //prechadzam vnutorny JSON Object, ktoreho kluc je vyssie ulozeny datum
                    for (Iterator<String> iterator = joDataForDate.keys(); iterator.hasNext(); ) {
                        measurementKey.add(iterator.next());
                        arrayWithData = joDataForDate.getJSONArray(measurementKey.get(j));
                        sensorValue = new SensorValue();
                        sensorValue.setId(arrayWithData.getInt(0));
                        //id datumu ukladam do premennej, lebo ju potrebujem este mimo tohto cyklu
                        //ulozit do sensor date
                        dateID = arrayWithData.getInt(1);
                        sensorValue.setDid(arrayWithData.getInt(1));
                        sensorValue.setValue(arrayWithData.getDouble(2));
                        sensorValue.setMeasurementID(Integer.parseInt(measurementKey.get(j)));
                        this.sensorValueList.add(sensorValue);
                        j++;
                    }
                    sensorDate = null;
                    sensorDate = new SensorDate();
                    sensorDate.setMeasurementDate(dateKey.get(i));
                    sensorDate.setId(dateID);
                    this.sensorDateList.add(sensorDate);
                }
            }
        }
    }

    /**
     * Rozparsovanie JSON odpovedi z APi pre entitu {@link Type}.
     *
     * @throws JSONException
     * @throws IOException
     */
    private void parseTypeResponse() throws JSONException, IOException {
        this.typeList = new ArrayList<>();
        String serverResponse = apiResponses.getTypeResponse();
        if (!serverResponse.isEmpty()) {
            JSONObject response = new JSONObject(serverResponse);
            JSONArray array = response.getJSONArray(DATA);
            Type type;
            for (int i = 0; i < array.length(); i++) {
                type = null;
                type = new Type();
                type.setId(array.getJSONArray(i).getInt(0));
                type.setTypeGroupID(array.getJSONArray(i).getInt(1));
                type.setText(array.getJSONArray(i).getString(4));
                this.typeList.add(type);
            }
        }
    }

    /**
     * Rozparsovanie JSON odpovedi z APi pre entitu {@link TypeGroup}.
     *
     * @throws JSONException
     * @throws IOException
     */
    private void parseTypeGroupResponse() throws JSONException, IOException {
        this.typeGroupList = new ArrayList<>();
        String serverResponse = apiResponses.getTypeGroupResponse();
        if (!serverResponse.isEmpty()) {
            JSONObject response = new JSONObject(serverResponse);
            JSONArray array = response.getJSONArray(DATA);
            TypeGroup typeGroup;
            for (int i = 0; i < array.length(); i++) {
                typeGroup = null;
                typeGroup = new TypeGroup();
                typeGroup.setId(array.getJSONArray(i).getInt(0));
                typeGroup.setText(array.getJSONArray(i).getString(1));
                typeGroup.setUnit(array.getJSONArray(i).getString(2));
                typeGroup.setUnitSI(array.getJSONArray(i).getString(3));
                this.typeGroupList.add(typeGroup);
            }
        }
    }

    /**
     * Vracia zoznam stiahnutych poloziek entity {@link Sensor}.
     *
     * @return zoznam {@link Sensor}
     * @throws IOException
     * @throws JSONException
     */
    public ArrayList<Sensor> getSensorList() throws IOException, JSONException {
        this.parseSensorResponse();
        return sensorList;
    }

    /**
     * Vracia zoznam stiahnutych poloziek entity {@link Sector}.
     *
     * @return zoznam {@link Sector}
     * @throws IOException
     * @throws JSONException
     */
    public ArrayList<Sector> getSectorList() throws IOException, JSONException {
        this.parseSectorResponse();
        return sectorList;
    }

    /**
     * Vracia zoznam stiahnutych poloziek entity {@link SensorSector}.
     *
     * @return zoznam {@link SensorSector}
     * @throws IOException
     * @throws JSONException
     */
    public ArrayList<SensorSector> getSensorSectorList() throws IOException, JSONException {
        this.parseSensorSectorResponse();
        return sensorSectorList;
    }

    /**
     * Vracia zoznam stiahnutych poloziek entity {@link Area}.
     *
     * @return zoznam {@link Area}
     * @throws IOException
     * @throws JSONException
     */
    public ArrayList<Area> getAreas() throws IOException, JSONException {
        this.parseAreaResponse();
        return areaList;
    }

    /**
     * Vracia zoznam stiahnutych poloziek entity {@link User}.
     *
     * @return zoznam {@link User}
     * @throws IOException
     * @throws JSONException
     */
    public ArrayList<User> getUsers() throws IOException, JSONException {
        this.parseUserResponse();
        return userList;
    }

    /**
     * Vracia zoznam stiahnutych poloziek entity {@link GroupView}.
     *
     * @return zoznam {@link GroupView}
     * @throws IOException
     * @throws JSONException
     */
    public ArrayList<GroupView> getGroupViews() throws IOException, JSONException {
        this.parseGroupViewResponse();
        return groupViewList;
    }

    /**
     * Vracia zoznam stiahnutych poloziek entity {@link Measurement}.
     *
     * @return zoznam {@link Measurement}
     * @throws IOException
     * @throws JSONException
     */
    public ArrayList<Measurement> getMeasurements() throws IOException, JSONException {
        this.parseMeasurementResponse();
        return measurementList;
    }

    /**
     * Vracia zoznam stiahnutych poloziek entity {@link SensorProperties}.
     *
     * @return zoznam {@link SensorProperties}
     * @throws IOException
     * @throws JSONException
     */
    public ArrayList<SensorProperties> getSensorProperties() throws IOException, JSONException {
        this.parseSensorPropertiesResponse();
        return sensorPropertiesList;
    }

    /**
     * Vracia zoznam stiahnutych poloziek entity {@link SensorDate}.
     *
     * @return zoznam {@link SensorDate}
     * @throws IOException
     * @throws JSONException
     */
    public ArrayList<SensorDate> getSensorDates(List<String> dates) throws IOException, JSONException {
        this.parseSensorDatesAndValuesResponse(dates);
        return this.sensorDateList;
    }

    /**
     * Vracia zoznam stiahnutych poloziek entity {@link SensorValue}.
     *
     * @return zoznam {@link SensorValue}
     * @throws IOException
     * @throws JSONException
     */
    public ArrayList<SensorValue> getSensorValues() {
        //uz sa nemusi parsovat lebo data uz boli rozparsovane skor v metode getSensorDates
        return this.sensorValueList;
    }

    /**
     * Vracia zoznam stiahnutych poloziek entity {@link Type}.
     *
     * @return zoznam {@link Type}
     * @throws IOException
     * @throws JSONException
     */
    public ArrayList<Type> getTypes() throws IOException, JSONException {
        this.parseTypeResponse();
        return typeList;
    }

    /**
     * Vracia zoznam stiahnutych poloziek entity {@link TypeGroup}.
     *
     * @return zoznam {@link TypeGroup}
     * @throws IOException
     * @throws JSONException
     */
    public ArrayList<TypeGroup> getTypeGroups() throws IOException, JSONException {
        this.parseTypeGroupResponse();
        return typeGroupList;
    }

    /**
     * Vycisti alokovane zoznamy a uvolni referencie.
     */
    public void clearApiDataLists() {
        if (this.userList != null) {
            this.userList.clear();
            this.userList = null;
        }
        if (this.sensorList != null) {
            this.sensorList.clear();
            this.sensorList = null;
        }
        if (this.sectorList != null) {
            this.sectorList.clear();
            this.sectorList = null;
        }
        if (this.areaList != null) {
            this.areaList.clear();
            this.areaList = null;
        }
        if (this.sensorSectorList != null) {
            this.sensorSectorList.clear();
            this.sensorSectorList = null;
        }
        if (this.measurementList != null) {
            this.measurementList.clear();
            this.measurementList = null;
        }
        if (this.sensorPropertiesList != null) {
            this.sensorPropertiesList.clear();
            this.measurementList = null;
        }
        if (this.groupViewList != null) {
            this.groupViewList.clear();
            this.groupViewList = null;
        }
        if (this.sensorDateList != null) {
            this.sensorDateList.clear();
            this.groupViewList = null;
        }
        if (this.sensorValueList != null) {
            this.sensorValueList.clear();
            this.sensorValueList = null;
        }
        if (this.typeList != null) {
            this.typeList.clear();
            this.typeList = null;
        }
        if (this.typeGroupList != null) {
            this.typeGroupList.clear();
            this.typeGroupList = null;
        }
    }
}

