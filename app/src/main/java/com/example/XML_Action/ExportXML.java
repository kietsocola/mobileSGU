package com.example.XML_Action;

import android.content.Context;
import android.util.Xml;
import android.widget.Toast;

import com.example.Model.User;
import com.example.apptichdiem.MainActivity;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExportXML {

    private Context context;

    // Constructor để truyền Context vào lớp
    public ExportXML(Context context) {
        this.context = context;
    }

    public void exportCustomersToXML(List<User> customerList) {
        // Tạo file XML trong bộ nhớ ngoài của ứng dụng
        File xmlFile = new File(context.getExternalFilesDir(null), "customers_list.xml");
        try {
            FileOutputStream fos = new FileOutputStream(xmlFile);
            XmlSerializer xmlSerializer = Xml.newSerializer();
            xmlSerializer.setOutput(fos, "UTF-8");
            xmlSerializer.startDocument("UTF-8", true);

            // Bắt đầu thẻ gốc "HanhKhachs"
            xmlSerializer.startTag(null, "Customers");

            for (User hanhKhach : customerList) {
                // Mỗi đối tượng HanhKhach sẽ là một thẻ "HanhKhach"
                xmlSerializer.startTag(null, "Customer");

                // id
                xmlSerializer.startTag(null, "phonenumber");
                xmlSerializer.text(String.valueOf(hanhKhach.getPhoneNumber())); // Chỉnh lại thành getId()
                xmlSerializer.endTag(null, "phonenumber");

                // hoTen
                xmlSerializer.startTag(null, "point");
                xmlSerializer.text(String.valueOf(hanhKhach.getPoint())); // Chỉnh lại thành getHoTen()
                xmlSerializer.endTag(null, "point");

                // soDienThoai
                xmlSerializer.startTag(null, "datecreate");
                xmlSerializer.text(hanhKhach.getDateCreate()); // Chỉnh lại thành getSoDienThoai()
                xmlSerializer.endTag(null, "datecreate");

                xmlSerializer.startTag(null, "dateupdate");
                xmlSerializer.text(hanhKhach.getDateUpdate()); // Chỉnh lại thành getSoDienThoai()
                xmlSerializer.endTag(null, "dateupdate");

                xmlSerializer.endTag(null, "Customer");
            }

            // Kết thúc thẻ gốc "HanhKhachs"
            xmlSerializer.endTag(null, "Customers");
            xmlSerializer.endDocument();

            xmlSerializer.flush();
            fos.close();

            System.out.println("File đã được lưu tại: " + xmlFile.getAbsolutePath());
            Toast.makeText(context.getApplicationContext(), "File đã được lưu tại: " + xmlFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(context.getApplicationContext(), "Lưu file lỗi", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}