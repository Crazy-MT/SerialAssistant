package com.sysout.app.serial.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SerialCommand implements Parcelable {
    @Id(autoincrement = true)
    private Long id;

    private String remarks;

    private String command;

    private Integer type;       // 0 - HEX; 1 - TXT;

    @Generated(hash = 871336629)
    public SerialCommand(Long id, String remarks, String command, Integer type) {
        this.id = id;
        this.remarks = remarks;
        this.command = command;
        this.type = type;
    }

    @Generated(hash = 2087779334)
    public SerialCommand() {
    }

    protected SerialCommand(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        remarks = in.readString();
        command = in.readString();
        if (in.readByte() == 0) {
            type = 0;
        } else {
            type = in.readInt();
        }
    }

    public static final Creator<SerialCommand> CREATOR = new Creator<SerialCommand>() {
        @Override
        public SerialCommand createFromParcel(Parcel in) {
            return new SerialCommand(in);
        }

        @Override
        public SerialCommand[] newArray(int size) {
            return new SerialCommand[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(remarks);
        dest.writeString(command);
        dest.writeInt(type);
    }

    @Override
    public String toString() {
        return "SerialCommand{" +
                "id=" + id +
                ", remarks='" + remarks + '\'' +
                ", command='" + command + '\'' +
                ", type=" + type +
                '}';
    }
}
