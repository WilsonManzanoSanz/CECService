package co.edu.uninorte.cec.mobile.cecservice.shared.objects;

import android.content.ContentValues;

/**
 * Created by Administrador on 06/05/2017.
 */

public class InsertSQLRequest {

    public String tableName;
    public ContentValues contentValues;
    public boolean successfully;
    public String errorMessage;
    public String userState;

}
