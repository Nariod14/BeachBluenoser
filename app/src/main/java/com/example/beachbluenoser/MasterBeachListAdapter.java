package com.example.beachbluenoser;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;



    public class MasterBeachListAdapter extends RecyclerView.Adapter<MasterBeachListAdapter.ListItem> {
        private ArrayList<BeachItem> beaches;




        /*
        /**
         * constructor
         * @param beches array of BeachItem objects to show on the list
         */
        public MasterBeachListAdapter(ArrayList<BeachItem> beaches) {
            this.beaches = beaches;
        }

        //Create new views (invoked by the layout manager)
        @Override
        public ListItem onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.beachitem, parent, false);

            return (new ListItem(v,beaches));
        }

        @Override
        public void onBindViewHolder(ListItem listItem, int position) {
            // - replace the contents of the view with that element
            int pos = beaches.size() - (position + 1);//to reverse order
            String beachImageFileName = beaches.get(pos).getImageSource();
            listItem.beachName.setText(beaches.get(pos).getName());
            Integer rating = beaches.get(pos).getRating();
            //set description and shorten length if needed
            String desc = beaches.get(pos).getDescription();
            /*
            if(desc.length() > 20){
                desc = desc.substring(0,20) + "...";
            }
            */
            listItem.beachRating.setText(rating+"/5");
            listItem.beachDescription.setText(desc);

            listItem.setBeachImage(beachImageFileName);



           // int ImageResource = mainView.getResources().getIdentifier(uri,null,getPackageName());
            // listItem.beachImage.setImageDrawable(R.drawable.theetcher);
            //listItem.setBeachImage();
            //listItem.beachRating.setText()
            /*
                    Set beach image
             */


        }


        public static class ListItem extends RecyclerView.ViewHolder {
            LinearLayout beachLayout;
            View mainView;
            TextView beachName;
            TextView beachDescription;
            TextView beachRating;
            ImageView beachImage;


            // TextView postTitle;
            //  TextView postDescription;
            //  ImageView jobIcon;

            ArrayList<BeachItem> beachList;

            public ListItem(View listItemView, ArrayList<BeachItem> beaches) {
                super(listItemView);
                beachList =beaches;
                mainView = listItemView;
                beachName = listItemView.findViewById(R.id.BeachName);
                beachDescription = listItemView.findViewById(R.id.BeachDescription);
                beachRating = listItemView.findViewById(R.id.BeachRating);
                beachImage = listItemView.findViewById(R.id.BeachImage);
                //beachImage

                beachLayout = listItemView.findViewById(R.id.beachItem);


                //make item clickable
                beachLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get position
                       // gotToBeachMasterPage();
                    }
                });
            }

            public void setBeachImage(String beachImageFileName){

                //Log.d("IMAGENAME: ","name : "+ beaches.get(pos).getImageSource());
               // beachImage.setImageResource(R.drawable.theetcher);
           //     beachImage.setImageURI("path/");
               // beachImage.setBackground(R.drawable.beachmeadows_beach);
                if(beachImageFileName.equals("")|| beachImageFileName == null){
                    beachImageFileName ="default1.jpg";
                }
                beachImageFileName = beachImageFileName.replace('-','_');
                int fileExtension = beachImageFileName.indexOf('.');

                Log.d("SetImage"," file before parse "+beachImageFileName);
                beachImageFileName = beachImageFileName.substring(0,fileExtension);
                String uri = "@drawable/"+beachImageFileName;
                Log.d("SetImage"," this is the file path: "+uri);
                int fileID =0;

                try{
                    fileID = R.drawable.class.getField(beachImageFileName).getInt(null);
                }catch(IllegalAccessException e){
                    Log.d("getImageIDError","Error getting image");
                }catch(NoSuchFieldException e2){
                    Log.d("getImageIDError","no Icon found");
                }
                beachImage.setImageResource(fileID);



                //int imageResouce = mainView.getResources().getIdentifier(uri,null,mainView.getActivty().getPackageName());
               // Drawable res = mainView.getResources().getDrawable(imageResouce);

            }

/*
           // When click on a container go to > page with context (intent)
            private void gotToBeachMasterPage(){
                Intent intent = new Intent(mainView.getContext(), *InsertClassHere*.class);
                int pos = beachList.size() - (getAdapterPosition() + 1);//to reverse order

                intent.putExtra("beachName",beachList.get(pos).getName());
                mainView.getContext().startActivity(intent);
            }
        */

        }






        @Override
        public int getItemCount() {

           // if (beaches!=null)
            return beaches.size();
          //  else
           //     return 0;
        }




}
