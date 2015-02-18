package edu.sxccal.qrcodescanner;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import edu.sxccal.qrcodescanner.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.os.Environment;
import java.io.*;

public class QRCode extends Activity implements OnClickListener
{

	private Button scanBtn,gen,ver,ab,dqr;
	public static TextView tv;
	private static final String TAG = "MEDIA";
	public static String scanContent="No result";
	public static final String filePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/QR";	
	
    
	protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        File dir=new File(QRCode.filePath);
        if(!dir.exists())
        	dir.mkdir();
        scanBtn = (Button)findViewById(R.id.scan_button);           
        tv=(TextView)findViewById(R.id.file_write);       
        ver=(Button)findViewById(R.id.ver_sig);
        ab=(Button)findViewById(R.id.ab);
        gen=(Button)findViewById(R.id.gen_qr);
        dqr=(Button)findViewById(R.id.decode);
        ab.setOnClickListener(this);    
        ver.setOnClickListener(this);         
        scanBtn.setOnClickListener(this);  
        gen.setOnClickListener(this);
        dqr.setOnClickListener(this);
    }	    
	public void onClick(View v)
	{
		if(v.getId()==R.id.scan_button)
		{
			tv.setText("");
			IntentIntegrator scanner = new IntentIntegrator(this);
			scanner.initiateScan();			
		}	
		if(v.getId()==R.id.ab)
		{
			Intent about=new Intent(QRCode.this,About.class);
			startActivity(about);
		}
		if(v.getId()==R.id.ver_sig)
		{
			Intent verify= new Intent(QRCode.this,Verify.class);                               
        	startActivity(verify);      
		}
		if(v.getId()==R.id.gen_qr)
		{
			Intent qr=new Intent(QRCode.this,GenQR.class);
			startActivity(qr);
		}
		if(v.getId()==R.id.decode)
		{
			Intent qr=new Intent(QRCode.this,DecodeQR.class);
			startActivity(qr);
		}
	}	
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		if(intent!=null)
		{
			IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);		
			if (scanningResult != null)
			{
				scanContent = scanningResult.getContents();		
				if(checkExternalMedia())
				{
					write_to_file();	        			
					String zipin=filePath+"/result.zip";
					String files[]=Unzip.unzip(zipin, filePath);
					if(files[2].equals(""))
						tv.setText("Image was not scanned properly.Try again!");
					else
						tv.setText("Decoded files are: \n"+files[0]+"\n"+files[1]+"\n"+files[2]);						
				}
				else
					tv.setText("Device doesn't support read/write!");
			}
			else
			{
			    Toast toast = Toast.makeText(getApplicationContext(),
			        "No scan data received!", Toast.LENGTH_SHORT);
			    toast.show();		    
			}	
		}		
	}   
	public boolean checkExternalMedia()
	{
		    boolean readable = false;
		    boolean writeable = false;
		    String state = Environment.getExternalStorageState();
		    if (Environment.MEDIA_MOUNTED.equals(state)) 
		    {		       
		        readable = writeable = true;
		    }
		    else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) 
		    {		        
		        readable = true;
		        writeable = false;
		    }
		    else
		    {		        
		        readable = writeable = false;
		    }   
		    return (readable&&writeable);
	}		
	public void write_to_file()
	{			 			
		    File dir = new File (filePath);		    
		    File file = new File(dir, "/result.zip");		    
		    try 
		    {
		        FileOutputStream f = new FileOutputStream(file);		        
		        for(int i=0;i<scanContent.length();++i)
		        	f.write(scanContent.charAt(i));		        		        
		        f.close();
		    } 
		    catch(Exception e)
		    {		    	
		    	Log.create_log(e, tv);
		    }
		    tv.append("\n\nFile written to "+file);
	}	
}     


