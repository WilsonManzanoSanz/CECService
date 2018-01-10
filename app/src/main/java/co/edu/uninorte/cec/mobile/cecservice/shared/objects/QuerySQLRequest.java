package co.edu.uninorte.cec.mobile.cecservice.shared.objects;

import java.util.Vector;

/**
 * Created by Administrador on 06/05/2017.
 */

public class QuerySQLRequest {

    public String queryString;
    public boolean successfullY;
    public String errorMessage;
    public String userState;

    public Vector<Object[]> result;
}
