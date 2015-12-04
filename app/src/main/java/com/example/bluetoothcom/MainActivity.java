package com.example.bluetoothcom;

import android.os.Bundle;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button On,Off,Visible,list;
	private BluetoothAdapter BA;
	private BluetoothSocket blueSocket;
	private BluetoothDevice blueDevice;
	private Set<BluetoothDevice> pairedDevices;
	private ListView lv;
	
	private int COMANDO_PARAR			= 80;
	private int COMANDO_RELAJAR 		= 82;
	private int COMANDO_TENSIONAR 		= 84;
	private int COMANDO_OBTENER_LIBRAS 	= 48;
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		On = (Button)findViewById(R.id.button1);
		Off = (Button)findViewById(R.id.button2);
		Visible = (Button)findViewById(R.id.button3);
		list = (Button)findViewById(R.id.button4);
		
		lv = (ListView)findViewById(R.id.listView1);
		
		BA = BluetoothAdapter.getDefaultAdapter();
		
	}
	
	public void on(View view){
		if (!BA.isEnabled()) {
			Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(turnOn, 0);
			Toast.makeText(getApplicationContext(),"Turned on" 
					,Toast.LENGTH_LONG).show();
		}
		else{
			Toast.makeText(getApplicationContext(),"Already on",
					Toast.LENGTH_LONG).show();
		}
	}
	public void list(View view) throws IOException{
		pairedDevices = BA.getBondedDevices();
		
		ArrayList list = new ArrayList();
		for(BluetoothDevice bt : pairedDevices)
		{	
			if(bt.getName().toString().equals("Traccion"))
			{
				blueSocket = createSocket(bt);
				blueSocket.connect();
			}
			list.add(bt.getName());
		}
		Toast.makeText(getApplicationContext(),"Showing Paired Devices",
				Toast.LENGTH_SHORT).show();
		final ArrayAdapter adapter = new ArrayAdapter
				(this,android.R.layout.simple_list_item_1, list);
		lv.setAdapter(adapter);

	}
	public void off(View view){
		BA.disable();
		Toast.makeText(getApplicationContext(),"Turned off" ,
				Toast.LENGTH_LONG).show();
	}
	public void visible(View view){
		Intent getVisible = new Intent(BluetoothAdapter.
				ACTION_REQUEST_DISCOVERABLE);
		startActivityForResult(getVisible, 0);

	}
	
	public static BluetoothSocket createSocket(final BluetoothDevice device) throws IOException {
		BluetoothSocket socket=null;
		try {
			Method m=device.getClass().getMethod("createRfcommSocket",int.class);
			socket=(BluetoothSocket)m.invoke(device,1);
		}
		catch (  NoSuchMethodException ignore) {
			socket=device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
		}
		catch (  Exception ignore) {
		}
		return socket;
	}
	
	public void eventoRelajar(View view) throws IOException{
		try {
			if( blueSocket.isConnected() )
				blueSocket.getOutputStream().write(COMANDO_RELAJAR);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.w("Socket_Exc", e.toString());
		}
	}
	
	public void eventoTensionar(View view){
		try {
			if( blueSocket.isConnected() )
				blueSocket.getOutputStream().write(COMANDO_TENSIONAR);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.w("Socket_Exc", e.toString());
		}
	}
	
	public void eventoParar(View view){
		try {
			if( blueSocket.isConnected() )
				blueSocket.getOutputStream().write(COMANDO_PARAR);
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.w("Socket_Exc", e.toString());
		}
	}
	
	public void eventoObtenerLibras(View view){
		try {
			if( blueSocket.isConnected() )
			{
				blueSocket.getOutputStream().write(COMANDO_OBTENER_LIBRAS);
				
				int dec = blueSocket.getInputStream().read();
				int uni = blueSocket.getInputStream().read();
				
				Log.w( "SocketIn", new StringBuilder().append(Character.toChars(dec)).append(Character.toChars(uni)).toString() );
				
				dec = blueSocket.getInputStream().read();
				uni = blueSocket.getInputStream().read();
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.w("Socket_Exc", e.toString());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
