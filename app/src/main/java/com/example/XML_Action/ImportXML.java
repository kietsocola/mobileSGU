package com.example.XML_Action;

import android.content.Context;
import android.net.Uri;
import android.util.Xml;
import android.widget.Toast;

import com.example.Model.User;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImportXML {

    private Context context;

    // Constructor để truyền Context
    public ImportXML(Context context) {
        this.context = context;
    }

    // Hàm để import danh sách khách hàng từ file XML
    public List<User> importCustomersFromXML() {
        List<User> customerList = new ArrayList<>();
        File xmlFile = new File(context.getExternalFilesDir(null), "hanhkhach_list.xml");

        try {
            FileInputStream fis = new FileInputStream(xmlFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(fis, "UTF-8");

            User user = null;
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("Customer".equals(tagName)) {
                            // Bắt đầu thẻ <Customer>
                            user = new User();
                        } else if (user != null) {
                            if ("phonenumber".equals(tagName)) {
                                user.setPhoneNumber(parser.nextText()); // Lấy số điện thoại
                            } else if ("point".equals(tagName)) {
                                user.setPoint(Integer.parseInt(parser.nextText())); // Lấy điểm
                            } else if ("datecreate".equals(tagName)) {
                                user.setDateCreate(parser.nextText()); // Lấy ngày tạo
                            } else if ("dateupdate".equals(tagName)) {
                                user.setDateUpdate(parser.nextText()); // Lấy ngày cập nhật
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if ("Customer".equals(tagName) && user != null) {
                            // Kết thúc thẻ <Customer>, thêm user vào danh sách
                            customerList.add(user);
                        }
                        break;
                }
                eventType = parser.next();
            }
            fis.close();
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return customerList;
    }
    public List<User> importCustomersFromXML(Uri uri) {
        List<User> customerList = new ArrayList<>();

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "UTF-8");

            User user = null;
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("Customer".equals(tagName)) {
                            user = new User();
                        } else if (user != null) {
                            if ("phonenumber".equals(tagName)) {
                                user.setPhoneNumber(parser.nextText());
                            } else if ("point".equals(tagName)) {
                                user.setPoint(Integer.parseInt(parser.nextText()));
                            } else if ("datecreate".equals(tagName)) {
                                user.setDateCreate(parser.nextText());
                            } else if ("dateupdate".equals(tagName)) {
                                user.setDateUpdate(parser.nextText());
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if ("Customer".equals(tagName) && user != null) {
                            customerList.add(user);
                        }
                        break;
                }
                eventType = parser.next();
            }
            inputStream.close();
        } catch (XmlPullParserException | IOException e) {
            Toast.makeText(context.getApplicationContext(), "Import file lỗi", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return customerList;
    }

}
