package com.example.beachbluenoser;

public class BeachItem {
    private String name;
    private String description;
    private String location;
    private String wheelChairRamp;
    private String capacity;
    private String sandyOrRocky;
    private String imageSource;
    private String visualWaterConditions;
    private Integer rating;
    public BeachItem(){

    }

    public BeachItem(String name,String imageSource,String capacity,String visualWaterConditions){
        this.name = name;
        this.wheelChairRamp = wheelChairRamp;
        this.capacity = capacity;
        this.sandyOrRocky = sandyOrRocky;
        this.imageSource = imageSource;
        this.rating = rating;
        this.visualWaterConditions = visualWaterConditions;
       // this.location = location;
    }

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public String getvisualWaterConditions(){return visualWaterConditions;}
    public void setvisualWaterConditions(String visualWaterConditions){this.visualWaterConditions = visualWaterConditions;}


    public String getwheelChairRamp(){return wheelChairRamp;}
    public void setwheelChairRamp(String wheelChairRamp){this.wheelChairRamp = wheelChairRamp;}

    public String getcapacity(){return capacity;}
    public void setcapacity(String capacity){this.capacity = capacity;}

    public String getsandyOrRocky(){return sandyOrRocky;}
    public void setsandyOrRocky(String sandyOrRocky){this.sandyOrRocky = sandyOrRocky;}



    public String getDescription(){return description;}
    public void setDescription(String description){this.description = description;}

    public String getImageSource(){return imageSource;}
    public void setImageSource(String imageSource){this.imageSource = imageSource;}

    public String getLocation(){return location;}
    public void setLocation(String location){this.location = location;}



}
