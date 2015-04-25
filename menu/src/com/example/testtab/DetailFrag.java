package com.example.testtab;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.testtab.ActionItem;
import com.example.testtab.QuickAction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 
 * so basically this class is for the second page or the listing of all selected items for the user
 * thought i could keep an instance of this fragment statically bound to the parent class 
 * but that was bat shit crazy with some wierd garbagecollector madness and some nullpointer exceptions that were not cool
 * so a fucking asshole of an implementation was done, as to why there is a fucking ArrayList that will be populated everytime
 * this class gets fucking created and this bitch will have to copy that fucking arraylist to a string array before it can attach it to the
 * fucking listview class...
 * why? because fuck you thats why.
 * @author WINDAdmin
 *
 */
public class DetailFrag extends Fragment {
	public byte[] ipAddr = new byte[] { (byte)192, (byte)168, (byte)0, (byte)200 };
	public BazaZamowien baza = new BazaZamowien();
	public String comments[] = new String[50];
	public Button button_print,button_menu;
	public ListView lv;
	ArrayList<BaseItem> myitemlist;
	public int n=0;
	public String strng="default";
	private SimpleAdapter arrayAdapter;
	List<Map<String, String>> items;
	delListener mCallback;
	numListener numCallback;
	private static final int ID_NON   = 1;
	private static final int ID_DEL   = 2;
	
	 // Container Activity must implement these interfaces
		// this listener is to inform the parent activity that an item has been deleted from the menu
	    public interface delListener {
	        public void onDelClick(int i);
	    }
	    
		// this listener is to inform the parent activity that an item's number 
	    public interface numListener {
	        public void onNumChange(int i,int p);
	    }
	    
	    @Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	        try {
	            mCallback = (delListener) activity;
	            numCallback = (numListener) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString()
	                    + " must implement Listeners!!");
	        }
	    }
	
	    // 2 constructsors 
	public DetailFrag()
	{
		
	}
	
	DetailFrag(ArrayList<BaseItem> s)
	{
		myitemlist=s;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// first prepare the list view to accept the items
        final View view = inflater.inflate(R.layout.detail, container, false);
        button_print =(Button) view.findViewById(R.id.button1);
        final TextView textNrZamowienia = (TextView) view.findViewById(R.id.textNrZamowienia);
        
        button_menu =(Button) view.findViewById(R.id.button2);
        ActionItem nextItem 	= new ActionItem(ID_DEL, "Kasuj zamówienie", getResources().getDrawable(R.drawable.menu_eraser));
		ActionItem prevItem 	= new ActionItem(ID_NON, "Anuluj ostatnie zamowienie", getResources().getDrawable(R.drawable.menu_info));
        
		prevItem.setSticky(true);
        nextItem.setSticky(true);
        final QuickAction quickAction = new QuickAction(getActivity(), QuickAction.VERTICAL);
		
		//add action items into QuickAction
        quickAction.addActionItem(nextItem);
		quickAction.addActionItem(prevItem);
        
		//Set listener for action item clicked
				quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {			
					@Override
					public void onItemClick(QuickAction source, int pos, int actionId) {				
						ActionItem actionItem = quickAction.getActionItem(pos);
		                 
						//here we can filter which action item was clicked with pos or actionId parameter
						if (actionId == ID_DEL) {
			    	    	while(myitemlist.size()>0) {
								items.remove(myitemlist.size()-1);
			    	    		myitemlist.remove(myitemlist.size()-1);
			    	    		arrayAdapter.notifyDataSetChanged();
			    	    		mCallback.onDelClick(myitemlist.size()-1);
			    	    	}
							Toast.makeText(getActivity(), "Usunieto biezace zamowienie.", Toast.LENGTH_SHORT).show();
						} else if (actionId == ID_NON) {
			            	Intent sendIntent = new Intent();
			            	String data = new String();
			            	sendIntent.setAction(Intent.ACTION_SEND);
							sendIntent.putExtra("Data","XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\n\nZamowienie nr "+(BazaZamowien.nrZamowienia-1)+"\nzostalo anulowane!\n\nXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\n\n\n\n\n");
				            sendIntent.setComponent(new ComponentName("qsrtech.posprintdriver","qsrtech.posprintdriver.printservice"));               
				            lv.getContext().startService(sendIntent);
				            data = Integer.toString(BazaZamowien.nrZamowienia-1)+";Zamowienie zostalo anulowane;;;;\n";
		                	baza.writeToFile(data);
							Toast.makeText(getActivity(), "Wyslano rezygnacje z ostatniego zamowienia.", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getActivity(), actionItem.getTitle() + " selected", Toast.LENGTH_SHORT).show();
						}
					}
				});
		
        
        lv = (ListView)view.findViewById(R.id.listview1);
        
        textNrZamowienia.setText(Integer.toString(BazaZamowien.nrZamowienia));
        String[] arr1 = new String[myitemlist.size()]; //Tablica nazw potraw
        String[] arr2 = new String[myitemlist.size()]; //Tablica ilosci
        String[] arr3 = new String[myitemlist.size()]; //Tablica komentarzy
        
        for(int i=0;i<myitemlist.size();i++)
        {	
        	arr1[i]=myitemlist.get(i).title;
           	arr2[i]="Ilosc: "+Integer.toString(myitemlist.get(i).num);
           	arr3[i]=myitemlist.get(i).comment;
           	comments[i] = "";
        }
        
        String[] from = new String[] { "str" ,"numbs","comment"};
    	int[] to = new int[] { R.id.textp1,R.id.textp2};
    	items =  new ArrayList<Map<String, String>>();

    	for ( int i = 0; i < arr1.length; i++ )
    	{
    	    Map<String, String> map = new HashMap<String, String>();
    	    map.put( "str", String.format( "%s", arr1[i] ) );
    	    map.put("numbs", String.format( "%s", arr2[i] ));
    	    map.put("comment", String.format( "%s", arr3[i] ) );
    	    items.add( map );
    	}
    	
    	arrayAdapter = new SimpleAdapter( getActivity(), items,R.layout.menulist, from, to );

        lv.setAdapter(arrayAdapter);
       
        // this is the single click listener.. :D inside it lies 
       lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View mview,
                    final int position, long id) {
            	Context mContext = getActivity();
            	LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            	View layout = inflater.inflate(R.layout.dialog,(ViewGroup) mview.findViewById(R.id.layout_root));
            	final NumberPicker np = (NumberPicker) layout.findViewById(R.id.numberPicker1);
                np.setMaxValue(10);
                np.setMinValue(1);
                np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                
                final EditText komentarz = (EditText)  layout.findViewById(R.id.PoleKoment);
                komentarz.setText(comments[position]);
            	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            	builder.setView(layout);
            	builder.setCancelable(true);
            	//builder.setIcon(R.drawable.dialog_question);
            	builder.setTitle("Zmien ilosc tego produktu lub dodaj komentarz");
            	builder.setInverseBackgroundForced(true);
            	// this is the dialog element that implements the changing number shz
            	builder.setPositiveButton("Potwierdz zmiane.", new DialogInterface.OnClickListener() {
            	  @Override
            	  public void onClick(DialogInterface dialog, int which) {
            		  
            		  numCallback.onNumChange(np.getValue(),position);
            		  Map<String, String> mss = items.get(position);
            		  mss.put("numbs", String.format( "Ilosc: %s", Integer.toString(np.getValue()) ));
            		  items.set(position, mss);
            		  arrayAdapter.notifyDataSetChanged();
            		  comments[position] = (String)komentarz.getText().toString();
            	  }
            	});
            	
            	AlertDialog alert = builder.create();
            	alert.show();

            	
                }
              });
       
       // long click to delete code
       lv.setLongClickable(true);
       lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
    	    @Override
    	    public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
    	    //	Toast.makeText(getActivity(), Integer.toString(pos), Toast.LENGTH_SHORT).show();
    	    	items.remove(pos);
    	    	myitemlist.remove(pos);
    	    	arrayAdapter.notifyDataSetChanged();
    	    	mCallback.onDelClick(pos);
    	        return true;
    	    }
    	});

        // Akcja dla przycisku wydruku
        button_print.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	
                String paragon = new String();
                String data = new String();
                int stringLength = 0;
                paragon = "    Restauracja u Bielawnego\n\n";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd              HH:mm:ss");
                String currentDateandTime = sdf.format(new Date());
                 sdf = new SimpleDateFormat("yyyy-MM-dd");
                String aktualnaData = sdf.format(new Date());
                 sdf = new SimpleDateFormat("HH:mm:ss");
                String aktualnyCzas = sdf.format(new Date());
                paragon = paragon + currentDateandTime + "\n";
                paragon = paragon + "Nr zamowienia: " + Integer.toString(BazaZamowien.nrZamowienia) + "\n\n";
                
            	//drukuj i zapisz do CSV      	
            	Intent sendIntent = new Intent();
            	sendIntent.setAction(Intent.ACTION_SEND);
                for(int i=0;i<myitemlist.size();i++)
                {	
                	if(comments[i].length()>1) comments[i] = "[" + comments[i] + "]";
                	stringLength = myitemlist.get(i).title.length()+comments[i].length()+3;
                   	paragon = paragon + myitemlist.get(i).title + " " + comments[i];
                   	if(stringLength%32<29)
                   		for(int j=0;j<(32-(stringLength%32))-3;j++)
                   		{	
                   			paragon = paragon + " ";
                   		}
                   		else
                       		for(int k=0;k<31;k++)
                       		{	
                       			paragon = paragon + " ";
                       		}
                   	paragon = paragon + "x"+Integer.toString(myitemlist.get(i).num) + "\n";
                    //przygotowanie danych do bazy zamowien
                	data = Integer.toString(BazaZamowien.nrZamowienia)+";"+myitemlist.get(i).title+";"+comments[i]+";"+Integer.toString(myitemlist.get(i).num)+";"+aktualnaData+";"+aktualnyCzas+"\n";
                	baza.writeToFile(data);
                }
                paragon = paragon + "--------------------------------\n\n\n\n\n";
                sendIntent.putExtra("Data",paragon);
            	sendIntent.setComponent(new ComponentName("qsrtech.posprintdriver","qsrtech.posprintdriver.printservice"));
            	if(myitemlist.size()>0) 
            	{
                    //wyswietl info o druku
                	Toast.makeText(getActivity(), "Trwa wydruk.", Toast.LENGTH_SHORT).show();
            		BazaZamowien.nrZamowienia++;
            		textNrZamowienia.setText(Integer.toString(BazaZamowien.nrZamowienia));
            		v.getContext().startService(sendIntent);
            		}
            	
                //usun biezace zamowienie
                while(myitemlist.size()>0) {
					items.remove(myitemlist.size()-1);
    	    		myitemlist.remove(myitemlist.size()-1);
    	    		arrayAdapter.notifyDataSetChanged();
    	    		mCallback.onDelClick(myitemlist.size()-1);
    	    	}        	
            }
            
        });
        
     // Akcja dla przycisku menu
        button_menu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	quickAction.show(v);
            }
            
        });
        
        
        
        return view;
	}
	

}
