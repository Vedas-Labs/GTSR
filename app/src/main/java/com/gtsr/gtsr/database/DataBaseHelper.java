package com.gtsr.gtsr.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.gtsr.gtsr.dataController.LanguageTextController;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DataBaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "GTSRTest.db";

    private static final int DATABASE_VERSION = 4;

    ConnectionSource objConnectionSource;

    private Dao<TestFactors,Integer> testFactorsesDao=null;
    private Dao<UrineresultsModel, Integer> urineresultsModelIntegerDao = null;

    public DataBaseHelper(Context contex) {
        super(contex, DATABASE_NAME, null, DATABASE_VERSION);
        getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        Log.e("DBStatusonCreate", "OnCreate" + connectionSource);
        try {
            TableUtils.createTable(connectionSource,UrineresultsModel.class);
            TableUtils.createTable(connectionSource,TestFactors.class);
            urineresultsModelIntegerDao = DaoManager.createDao(connectionSource, UrineresultsModel.class);
            testFactorsesDao = DaoManager.createDao(connectionSource,TestFactors.class);
            objConnectionSource = connectionSource;
         //   Toast.makeText(LanguageTextController.getInstance().context.getApplicationContext(),"Database Created", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        Log.e("oldVersion", "OnCreate" + oldVersion);
        Log.e("newVersion", "OnCreate" + newVersion);
        if(oldVersion == 3) {
            try {
                Dao dao = getUrineresultsDao();
                 dao.executeRaw("ALTER TABLE `UrineresultsModel` ADD COLUMN userName STRING;");
            } catch (SQLException e) {
                Toast.makeText(LanguageTextController.getInstance().context.getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
           /* try {
                Dao dao = getUrineresultsDao();
               // dao.executeRaw("ALTER TABLE `UrineresultsModel` ADD COLUMN userName STRING;");
                db.execSQL("DROP TABLE IF EXISTS `UrineresultsModel`");
                onCreate(db);
               // dao.executeRaw("ALTER TABLE `Card` ADD COLUMN validFrom STRING;");
            } catch (SQLException e) {
                Toast.makeText(LanguageTextController.getInstance().context.getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
            }*/
    }

    /*@Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        Log.e("DBStatus", "OnUpgrade" + connectionSource);
        try {
            TableUtils.dropTable(connectionSource, UrineresultsModel.class, true);
            TableUtils.dropTable(connectionSource, TestFactors.class, true);

            onCreate(database, connectionSource);
            objConnectionSource = connectionSource;
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }*/
    public Dao<UrineresultsModel, Integer> getUrineresultsDao() {
        if (urineresultsModelIntegerDao == null) {
            try {
                urineresultsModelIntegerDao = getDao(UrineresultsModel.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return urineresultsModelIntegerDao;
    }
    public Dao<TestFactors, Integer> getTestFactorsesDao() {
        if (testFactorsesDao == null) {
            try {
                testFactorsesDao = getDao(TestFactors.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return testFactorsesDao;
    }

}
