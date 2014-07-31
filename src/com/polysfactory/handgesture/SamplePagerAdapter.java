package com.polysfactory.handgesture;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.glass.app.Card;


public class SamplePagerAdapter extends PagerAdapter {
	
    private Context mContext;
    
    private static final String menus[] = new String[] { 
    	
    	//all the protocols steps are hardcoded
    	
    	/**** MOCLO LEVEL 1 AND 2 ****/
    	"MoClo Level 1 and 2",  //title
    	"Fill in Setup Sheet",  //step 1
    	"Follow instructions on Dilutions_PRINT sheet",
    	"If dilutions are already done, skip to REACTION SET-UP ahead.", 
    	"Turn on PCR machine", 
    	"Thaw DNA samples",
    	"Place enzymes in blue -20 deg Celsius freezer box and place on bench", 
    	"Thaw 10x Promega ligase buffer on ice ",
    	"Obtain 1 PCR tube per reaction and label", 
    	"Add 2uL of Promega ligation buffer (10x) to each tube",
    	"Add __uL of each part\n(1 for Level 0, up to 4 for Level 1 and 2) to each tube", 
    	"Add ___ uL  destination vector to each tube", //10    	
    	"Add ___ uL sterile H2O to each tube", 
    	"Add 0.5 uL of Promega T4 ligase enzyme to each tube", 
    	"Add __uL of BsaI (Level 1) or BbsI (Level 2) to each tube", 
    	"Close PCR tubes and spin them to collect liquid at the bottom of tube",
    	"Incubate as follows in PCR machine: ", "Level 1 or 2: \n25 cycles of [37 deg Celsius 1.5min, 16 deg Celsius 3min],"
        		+ "50 deg Celsius 5min, 80 deg Celsius 10min, hold at 4 deg Celsius", 
        "Use in transformation or store at -20 deg Celsius until use",
        		
        
        /**** TRANSFORMATION ****/
        "Transformation",
        "Thaw competent cells on ice (1-2 reactions/tube - 50uLs cells)",
        "Gently mix cells by flicking the tube",
        "Aliquot cells into 1.5mL microcentrifuge tubes (25uLs or 50uLs) - keep on ice",
        "Add 2uLs of ligation solution to the cells ",
        "Incubate cells on ice for 10 minutes",
        "Obtain 1 plate per reaction - make sure you use the correct antibiotic - place in 37 deg Celsius to warm",
        "Turn heat block on to 42 deg Celsius while cells incubate on ice",
        "Heat shock cells for 30-45seconds at 42C",
        "Return cells to ice for 5 minutes",
        "Aseptically add 950uLs SOC broth to each tube",
        "Recover at 37C shaking at 300rpm for 1 hour",
        "Spin down cells and remove 875uLs liquid for real and positive reactions\n  8000rpm for 3 mins",
        "Vortex cells to resuspend in remaining 100uLs of media",
        "Spread plate 100uLs on specified plates and incubate as follows:\n37 deg Celsius overnight\n  On the benchtop over the weekend",
        
        
        /**** EPOCH SPIN MINIPREP ****/
        "Epoch: Spin Miniprep Protocol",
        "Add RNase A to Buffer MX1 as directed on bottle and store at 4 deg. Celsius until use",
        "Add ethanol to Buffer WN and WS as directed on bottle",
        "Obtain Buffer MX1 from 4 deg. Celcius",
        "Centrifuge 2mLs of overnight culture 3 min at 8,000 x rpm",
        "Remove supernatent",
        "Add 200uLs of Buffer MX1 and resuspend pellet completely by vortexing",
        "Add 250uLs of Buffer MX2 and mix by inverting 4-6 times (DON'T vortex! Will give gDNA contam)",
        "Incubate at room temperature for 3 min.",
        "Add 350uLs of Buffer MX3 and mix by inverting 4-6 times",
        "Centrifuge for 10 mins at 13,000 rpm",
        "Place spin column in provided 2mL collection tube",
        "Decant sample into the spin column",
        "Centrifuge for 1 min at 5000 x rpm",
        "Discard flow-through and return column to collection tube",
        "500uL buffer WN to column",
        "Centrifuge for 1 min at 9000 x rpm",
        "Discard flow-through and return column to collection tube",
        "Add 700 uL Buffer WS to column",
        "Centrifuge for 1 min at 9000 x rpm",
        "Discard flow through and return column to collection tube",
        "Centrifuge for 1 min at 13000 x rpm",
        "Discard collection tube",
        "Place spin column into clean 1.5mL centrifuge tube(not supplied)",
        "Add 50uL Elution Buffer directly to column",
        "Let column stand for 2 mins at room temp",
        "Centrifuge for 1 min at 13,000 x rpm",
        "Use immediately or store plasmid at -20 deg Celsius until use",
    
        
        /**** LB BROTH ****/
        "LB Broth",
        "Obtain bottle of LB Broth media powder",
        "Obtain clean glass bottle with screw cap\nNOTE: bottle volume must be at least twice the volume you intend to make",
        "Label bottle with lab tape: name of media, date and your initials",
        "Weigh out media:\n20g/1L = 0.02 g per 1mL of water\n__ mLs water x 0.02 g = __ g to weigh",
        "Volume of water: _________",
        "Amount of LB Broth powder: __________",
        "Mix powder with water in bottle until fully dissolved.\nClose bottle securely and shake to mix",
        "Loosen cap - DO NOT SKIP THIS STEP",
        "Autoclave on liquid setting (setting 1 for <500mL, setting 2 for >500mL)",
        "Close cap and store on bench until use",
        
        
        /**** LB AGAR ****/
        "LB Agar",
        "Obtain bottle of LB Agar media powder",
        "Obtain clean glass flask\nNOTE: flask volume must be 300mLs or more the volume you intend to make",
        "Label flask with lab tape: name of media, date and your initials",
        "Obtain stir bar and place in flask - DO NOT SKIP THIS STEP",
        "Weigh out media as described below\n35g/1L = 0.035 g per 1mL of water\n____mLs water x 0.035 g = ____ g to weigh",
        "Volume of water: _________",
        "Amount of LB Agar powder: __________",
        "Cover flask top with tin foil and stick autoclave tape on top",
        "Mix powder with water in flask until fully dissolved\nPlace flask on stir plate and stir on medium speed until dissolved",
        "Autoclave on liquid setting (setting 1 for <500mL, setting 2 for >500mL)",
        "Spin on medium speed until temperature is safe to the touch (~30-45mins)",
        "If making antibiotic plates:",
        "Pull out 30-35 plates per 700mLs media and label side with black stripes\n"
                + "1 stripe = plain LB\n2 stripes = LB + Kanamycin\n3 stripes = LB + Ampicillin\n4 stripes = LB + Chloramphenicol",
        "While media cools, thaw 1000x stock of antibiotic on bench",
        "Add antibiotic once cool as follows:",
        "Add \"x\"uL of antibiotic to \"x\"mL of agar to get 1x concentration\nEx: 700uL of 1000x antibiotic to 700mLs of agar",
        "Once media is cooled and antibiotics added (if applicable), pour plates"
                + "using aseptic technique - pour agar into plates until it completely covers bottom of plate",
        "Let plates sit on bench to harden (~1 hour)",
        "Once hardened, flip plates upsidedown and slide bag over the stack",
        "Label the bag and store at 4 degrees Celsius until use\nLabel with your initials, date, type of plate (ex: LB + Amp)"
        
    };
    
    //footnotes to help user track where they are in the protocol. the first is a blank string because no footnote is needed for title screens(with pics)
    private static final String footnotes[] = new String[] {
    	/**** MOCLO LEVEL 1 AND 2 ****/ //0-18
    	"", "1/18", "2/18","3/18", "4/18", "5/18",	"6/18", "7/18", "8/18", "9/18", "10/18", "11/18",
    	"12/18", "13/18", "14/18","15/18", "16/18", "17/18","18/18",
    
    	/**** TRANSFORMATION ****/ //19-33
    	"", " 1/14", "2/14", " 3/14", " 4/14"," 5/14", " 6/14", " 7/14", " 8/14",
    	" 9/14", " 10/14", " 11/14", " 12/14"," 13/14", " 14/14",
    	
    	/**** EPOCH SPIN MINI PREP ****/ //34-61
    	"", "Before prep 1/2", "Before prep 2/2", "1/25", 
    	"2/25", "3/25", "4/25", "5/25","6/25", "7/25", "8/25", "9/25", "10/25", "11/25", "12/25", "13/25", 
    	"14/25", "15/25", "16/25", "17/25","18/25", "19/25", "20/25", "21/25","22/25", "23/25", "24/25", "25/25",
    
    	/**** LB BROTH ****/ //62-72
    	"", "1/10", "2/10", "3/10", "4/10", "5/10", "6/10", "7/10", "8/10", "9/10", "10/10", 
    	
    	/**** LB AGAR ****/ //73-93
    	"", "1/20", "2/20",   "3/20",  "4/20",  "5/20",  "6/20",  "7/20",  "8/20",  "9/20",  "10/20", 
    	 "11/20",  "12/20",  "13/20",  "14/20",  "15/20",  "16/20",  "17/20",  "18/20",  "19/20",  "20/20"
    }; 
    
    
  //places images only in the title screens. 
    private static final int images[] = new int[] {
    	R.drawable.moclo, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    	R.drawable.transformation, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	R.drawable.epochspin, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	R.drawable.lbbroth, 0,0,0,0,0,0,0,0,0,0,
    	R.drawable.lbagar, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
  };
    
    public SamplePagerAdapter(Context context) {
        mContext = context;
    }

    @Override
 
    public Object instantiateItem(ViewGroup container, int position) {
    	Card card = new Card(mContext);  
        String footnotetitle = "";
        if (position > 0 && position < 19) footnotetitle = "Moclo";
        else if (position > 19 && position < 34) footnotetitle = "Transformation";
        else if (position > 34 && position < 62) footnotetitle = "Epoch Spin Miniprep";
        else if (position > 62 && position < 73) footnotetitle = "LB Broth";
        else if (position > 73 && position < 94) footnotetitle = "LB Agar";
        
      //cases that only 1 card is displayed
        if (MainActivity.showmultcards == false || position == 0 || position == 19 || position == 34 || position == 62 || position == 73) {
        	card.setText(menus[position]);
        	//only add image for title cards
	        if (images[position] != 0){
	       	 card.setImageLayout(Card.ImageLayout.FULL);
	       	 card.addImage(images[position]);
	        }
	        card.setFootnote(footnotetitle + " " + footnotes[position]);  
	      //else, set the text of 2 cards
        }else {
	   		card.setText(menus[position] + "\n================\n" + menus[position+1]);
	   		card.setFootnote(footnotetitle + " " + footnotes[position] + " & " + footnotes[position+1]); 
	   	}
   	
        View view = card.getView();
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
    	 //total number of cards, not sure why length of array doesn't work
        return 94;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}