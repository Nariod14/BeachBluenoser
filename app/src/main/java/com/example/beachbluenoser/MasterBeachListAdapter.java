package com.example.beachbluenoser;


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
        public static class ListItem extends RecyclerView.ViewHolder {
            LinearLayout beachLayout;

            TextView beachName;
            TextView beachDescription;
            TextView beachRating;
            ImageView beachImage;


            // TextView postTitle;
            //  TextView postDescription;
            //  ImageView jobIcon;

            View mainView;
            ArrayList<BeachItem> beachList;

            public ListItem(View listItemView, ArrayList<BeachItem> beaches) {
                super(listItemView);
                beachList =beaches;
                mainView = listItemView;
                beachName = listItemView.findViewById(R.id.BeachName);
                beachDescription = listItemView.findViewById(R.id.BeachDescription);
                beachRating = listItemView.findViewById(R.id.BeachRating);
                //beachImage

                beachLayout = listItemView.findViewById(R.id.beachItem);


                //make item clickable
                beachLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get position
                        // gotToPostATask();
                    }
                });
            }

            /*
            When click on a container go to > page with context (intent)
            private void gotToPostATask(){
                Intent intent = new Intent(mainView.getContext(), PostATaskActivity.class);
                int pos = myPosts.size() - (getAdapterPosition() + 1);//to reverse order
                intent.putExtra("postID",myPosts.get(pos).getPostId());
                intent.putExtra("visibilityCheck",true);
                intent.putExtra("isMine",true);
                mainView.getContext().startActivity(intent);
            }
*/

        }

        @Override
        public void onBindViewHolder(ListItem listItem, int position) {
            // - replace the contents of the view with that element
            int pos = beaches.size() - (position + 1);//to reverse order

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
            //listItem.beachRating.setText()
            /*
                    Set beach image
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
