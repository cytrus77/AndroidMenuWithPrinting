package com.example.testtab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class BazaZamowien  extends MainActivity{
	
	public static int nrZamowienia;
	public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
	public String aktualnaData = sdf.format(new Date());
	public String dir = Environment.getExternalStorageDirectory()+File.separator+"RestauracjaBolo";
	public File folder = new File(dir); //folder name
	public File file = new File(folder, aktualnaData+".csv");
	public FileOutputStream newFile = null;

	BazaZamowien()
	{
		this.nrZamowienia = this.odczytNrZamowienia();
	}
	
	
	public void tworzeniePliku()
	{
		String data = new String();
		sdf = new SimpleDateFormat("yyyy-MM");
		aktualnaData = sdf.format(new Date());
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
		    //handle case of no SDCARD present
		} else {
		    //create folder
		    folder.mkdirs();	   
		    //create file
		     {
				try {
					newFile = new FileOutputStream(file,true);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				data = "Baza zamowien z "+aktualnaData+"\n";
				try {
					newFile.write(data.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				data = "Nr zamowienia;Potrawa;Komentarz;Ilosc;Data zamowienia;Godzina zamowienia\n";
				try {
					newFile.write(data.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		     }
			}
	}
	
	public int odczytNrZamowienia()
	{
		int nr = 0;
		sdf = new SimpleDateFormat("yyyy-MM");
		aktualnaData = sdf.format(new Date());
		
		FileInputStream in = null;
		if (file.exists()){
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine = null, tmp;
        try {
			while ((tmp = br.readLine()) != null) {
			    strLine = tmp;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        String lastLine = strLine;
        System.out.println(lastLine);
        try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Matcher matcher = Pattern.compile("\\d*").matcher(strLine);
        matcher.find();
       nr = Integer.valueOf(matcher.group());
		}
		return ++nr;
	}
		
	
	public void writeToFile(String data) {
		if (!file.exists()){
			this.tworzeniePliku();
		}
		
		try {
			newFile = new FileOutputStream(file,true);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			newFile.write(data.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			newFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
