package com.example.beachbluenoser;

public class BeachItem {
    private String name;
    private String description;
    private String location;
    private String imageSource;
    private Integer rating;
    public BeachItem(){

    }

    public BeachItem(String name,String imageSource,Integer rating){
        this.name = name;

        this.imageSource = imageSource;
        this.rating = rating;
       // this.location = location;
    }

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public String getDescription(){return description;}
    public void setDescription(String description){this.description = description;}

    public String getImageSource(){return imageSource;}
    public void setImageSource(String imageSource){this.imageSource = imageSource;}

    public String getLocation(){return location;}
    public void setLocation(String location){this.location = location;}

    public Integer getRating(){return rating;}
    public void setRating(Integer rating){this.rating = rating;}


}
