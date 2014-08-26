package com.wisewells.sdk.service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.wisewells.sdk.BeaconTracker;
import com.wisewells.sdk.beacon.BeaconVector;
import com.wisewells.sdk.beacon.DistanceVector;
import com.wisewells.sdk.beacon.Region;

public class LocationTopology extends Topology implements Parcelable {

	private final static double ITER_PER_SEC = 10D; //Number of Quasi-Newton iterations for a second
	private final static int MAX_ITER = 100; //Maximum number of iterations in one update

	private BeaconVector mBeaconVector;
	private Coordinate[] mCoordinates;
	private Coordinate mCurrentCorrdinate; //Current coordinate
	
	private long mLastUpdate; //in nanoseconds
	
	public static final Creator<LocationTopology> CREATOR = new Creator<LocationTopology>() {
		@Override
		public LocationTopology[] newArray(int size) {
			return new LocationTopology[size];
		}
		@Override
		public LocationTopology createFromParcel(Parcel source) {
			return new LocationTopology(source);
		}
	};
	
	public LocationTopology (int id, int type, String groupCode, 
			String serviceCode, String updateDate, String updateTime,
			BeaconVector beaconVector, Coordinate[] coordinates) {
		
		super(id, type, groupCode, serviceCode, updateDate, updateTime);
		mBeaconVector = beaconVector;
		mCoordinates = coordinates;
	}
	
	private LocationTopology(Parcel in) {
		super(in);
		mBeaconVector = in.readParcelable(BeaconVector.class.getClassLoader());
		in.readTypedArray(mCoordinates, Coordinate.CREATOR);
	}

	@Override
	public int describeContents() {
		return super.describeContents();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeParcelable(mBeaconVector, 0);
		dest.writeTypedArray(mCoordinates, 0);
	}
	
	public Coordinate getCoordinate(Region beacon)
	{
		int ind = mBeaconVector.indexOf(beacon);
		return (ind >= 0) ? mCoordinates[ind] : null;
	}
	
	public boolean setCoordinate(Region beacon, double x, double y)
	{
		int ind = mBeaconVector.indexOf(beacon);
		if(ind == -1) return false;
		mCoordinates[ind].set(x, y);
		return true;
	}

	@Override
	public String getTypeName() {
		return "Location";
	}
	
	@Override
	public Coordinate getResult()
	{
		DistanceVector dv = mTracker.getAvgDist(mBeaconVector);
		ArrayList<Coordinate> coord = new ArrayList<Coordinate>();
		ArrayList<Double> dist = new ArrayList<Double>();
		for(int ind = 0; ind < dv.getSize(); ind ++) {
			if(dv.get(ind) != null) {
				coord.add(mCoordinates[ind]);
				dist.add(dv.get(ind));
			}
		}
		if(dist.size() <= 2) return null;
		int numIter;
		long curTime = System.nanoTime();
		if(mLastUpdate == -1) {
			numIter = MAX_ITER;
		} else {
			double timeDiffMillis = (double)TimeUnit.NANOSECONDS.toMillis(curTime - mLastUpdate);
			numIter = Math.min(MAX_ITER, Math.max(1, (int)(ITER_PER_SEC * timeDiffMillis / 1000D)));
		}
		for(int it = 0; it < numIter; it ++) {
			quasiNewton(coord, dist);
		}
		return new Coordinate(mCurrentCorrdinate.getX(), mCurrentCorrdinate.getY());
	}
	
	private void quasiNewton(ArrayList<Coordinate> coord, ArrayList<Double> dist) {
		int size = dist.size(); 
		double[] xJ = new double[size];
		double[] yJ = new double[size];
		double[] err = new double[size];
		for(int ind = 0; ind < size; ind ++) {
			double d = mCurrentCorrdinate.getDist(coord.get(ind));
			Coordinate t = mCurrentCorrdinate.minus(coord.get(ind));
			xJ[ind] = t.getX()/d;
			yJ[ind] = t.getY()/d;
			err[ind] = d - dist.get(ind);
			Log.d("quasiNewton", "xJ[" + String.valueOf(ind) + "]: " + String.valueOf(xJ[ind]));
			Log.d("quasiNewton", "yJ[" + String.valueOf(ind) + "]: " + String.valueOf(yJ[ind]));
			Log.d("quasiNewton", "err[" + String.valueOf(ind) + "]: " + String.valueOf(err[ind]));
			
		}
		//Calculating matrix [a b; b d]
		double a = vecMul(xJ,xJ);
		double b = vecMul(xJ,yJ);
		double d = vecMul(yJ,yJ);
		//Calculating vector [e f]
		double e = vecMul(xJ,err);
		double f = vecMul(yJ,err);
		//Calculating delta
		double det = a*d - b*b;
		double deltaX = (d*e - b*f)/det;
		double deltaY = (a*f - b*e)/det;
		//Update current position
		double newX = mCurrentCorrdinate.getX() - deltaX;
		double newY = mCurrentCorrdinate.getY() - deltaY;
		if(!Double.isNaN(newX) && !Double.isNaN(newY)) mCurrentCorrdinate.set(newX, newY);		
	}
	
	//Vector multiplication
	private double vecMul(double[] a, double[] b) {
		int size = Array.getLength(a);
		double result = 0;
		for(int ind = 0; ind < size; ind ++) result += a[ind]*b[ind];
		return result;
	}

	public static class Coordinate implements Parcelable{
		private double x, y;
		public static final Creator<Coordinate> CREATOR = new Creator<LocationTopology.Coordinate>() {
			@Override
			public Coordinate[] newArray(int size) {
				return new Coordinate[size];
			}
			@Override
			public Coordinate createFromParcel(Parcel source) {
				return new Coordinate(source);
			}
		};
		
		public Coordinate(double nX, double nY) {
			x = nX;
			y = nY;
		}

		public void set(double nX, double nY) {
			x = nX;
			y = nY;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public double getDist(Coordinate c) {
			return Math.sqrt(Math.pow(this.x - c.x, 2) + Math.pow(this.y - c.y, 2));
		}

		public Coordinate minus(Coordinate c) {
			return new Coordinate(this.x - c.x, this.y - c.y);
		}
		
		private Coordinate(Parcel in) {
			x = in.readDouble();
			y = in.readDouble();
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeDouble(x);
			dest.writeDouble(y);
		}
	}
}





