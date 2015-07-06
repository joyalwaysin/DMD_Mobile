package com.nagainfo.zipmanagement;

import android.util.Log; 
import java.io.File; 
import java.io.FileInputStream; 
import java.io.FileOutputStream; 
import java.io.IOException;
import java.util.zip.ZipEntry; 
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream; 

/** 
 * 
 * @author jon 
 */ 
public class Decompress { 
  private String _zipFile; 
  private String _location; 

  public Decompress(String zipFile, String location) { 
    _zipFile = zipFile; 
    _location = location; 

    _dirChecker(""); 
  } 

  public void unzip() throws IOException,ZipException { 
    try  { 
      FileInputStream fin = new FileInputStream(_zipFile); 
      ZipInputStream zin = new ZipInputStream(fin); 
      ZipEntry ze = null; 
      byte buffer[] = new byte[8192];
      int bytesRead;
      while ((ze = zin.getNextEntry()) != null) { 
        Log.i("Decompress", "Unzipping " + ze.getName()); 

        if(ze.isDirectory()) { 
          _dirChecker(ze.getName()); 
        } else { 
          File file1=new File(_location + ze.getName());
          if(!file1.exists())
          {
        	  new File(file1.getParent()).mkdirs();
        	  file1.createNewFile();
          }
          
          
          
          FileOutputStream fout = new FileOutputStream(_location + ze.getName()); 
        
//          for (int c = zin.read(); c != -1; c = zin.read()) { 
//            fout.write(c); 
//          } 
          while ((bytesRead = zin.read(buffer)) != -1) {
        	  fout.write(buffer, 0, bytesRead);
			}
          zin.closeEntry(); 
          fout.close(); 
        } 

      } 
      zin.close(); 
    } catch(Exception e) { 
      Log.e("Decompress", "unzip", e); 
    } 

  } 

  private void _dirChecker(String dir) { 
    File f = new File(_location + dir); 

    if(!f.isDirectory()) { 
      f.mkdirs(); 
    } 
  } 
}
