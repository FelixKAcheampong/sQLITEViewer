package com.felinkotech.flxsqlitemanager.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Table implements Parcelable {
    private String tableName ;
    private int numberOfRecords ;

    public Table(){}

    protected Table(Parcel in) {
        tableName = in.readString();
        numberOfRecords = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tableName);
        dest.writeInt(numberOfRecords);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Table> CREATOR = new Creator<Table>() {
        @Override
        public Table createFromParcel(Parcel in) {
            return new Table(in);
        }

        @Override
        public Table[] newArray(int size) {
            return new Table[size];
        }
    };

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getNumberOfRecords() {
        return numberOfRecords;
    }

    public void setNumberOfRecords(int numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }
}
