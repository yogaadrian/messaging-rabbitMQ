/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 *
 * @author yoga
 */
public class Message implements java.io.Serializable {

    private int type;
    private String sender;
    private String content;
    private String namagroup;
    private ArrayList<String> userids;

    private ArrayList<String> listgroup;

    private ArrayList<String> listfriend;

    private String friendid;
//    private String command;
//    private String userid;
//    private String password;

    // 0 untuk pm, 1 untuk group, 2 untuk command
    public Message(int type, String sender, String content) {
        this.type = type;
        this.sender = sender;
        this.content = content;
        userids = null;
        listfriend = null;
        listgroup = null;
//        this.command = null;
//        this.userid = null;
//        this.password = null;

    }

    public static Message toMessage(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        Message m;
        try {
            in = new ObjectInputStream(bis);
            m = (Message) in.readObject();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return m;
    }

    public int getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public String getGroupName() {
        return namagroup;
    }

    public String getFriendID() {
        return friendid;
    }

    public ArrayList<String> getListUser() {
        return userids;
    }

    public ArrayList<String> getListFriend() {
        return listfriend;
    }

    public ArrayList<String> getListGroup() {
        return listgroup;
    }

//    public String getCommand(){
//        return command;
//    }
//    public String getUserID(){
//        return userid;
//    }
//    public String getPassword(){
//        return password;
//    }
    public void setType(int vtype) {
        type = vtype;
    }

    public void setSender(String vsender) {
        sender = vsender;
    }

    public void setContent(String vcontent) {
        content = vcontent;
    }

    public void setGroupName(String vnamagroup) {
        namagroup = vnamagroup;
    }

    public void setFriendID(String vfriendid) {
        friendid = vfriendid;
    }

    public void setUserIDs(ArrayList<String> vuserids) {
        userids = new ArrayList<String>(vuserids);
    }

    public void setListFriend(ArrayList<String> vlistfriend) {
        listfriend = new ArrayList<String>(vlistfriend);

    }

    public void setListGroup(ArrayList<String> vlistgroup) {
        listgroup = new ArrayList<String>(vlistgroup);


    }
//    public void setCommand(String vcommand){
//        command=vcommand;
//    }
//    public void setUserID(String vuserid){
//        userid=vuserid;
//    }
//    public void setPassword(String vpassword){
//        password=vpassword;
//    }

    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
            return bos.toByteArray();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

}
