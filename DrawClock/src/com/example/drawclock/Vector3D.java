package com.example.drawclock;

public class Vector3D
{
  public static final Vector3D NaN = new Vector3D((0.0F / 0.0F), (0.0F / 0.0F), (0.0F / 0.0F));
  public static final Vector3D ZERO = new Vector3D(0.0F, 0.0F, 0.0F);
  private final float x;
  private final float y;
  private final float z;

  public Vector3D(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this.x = paramFloat1;
    this.y = paramFloat2;
    this.z = paramFloat3;
  }

  public Vector3D add(float paramFloat, Vector3D paramVector3D)//向量乘以标量后相加
  {
    Vector3D localVector3D = paramVector3D.scalarMultiply(paramFloat);
    float f1 = localVector3D.getX();
    float f2 = localVector3D.getY();
    float f3 = localVector3D.getZ();
    return new Vector3D(f1 + this.x, f2 + this.y, f3 + this.z);
  }

  public Vector3D add(Vector3D paramVector3D)//向量相加
  {
    float f1 = paramVector3D.getX();
    float f2 = paramVector3D.getY();
    float f3 = paramVector3D.getZ();
    return new Vector3D(f1 + this.x, f2 + this.y, f3 + this.z);
  }

  public float angle(Vector3D paramVector3D)//两向量夹角
  {
    double d1 = norm() * paramVector3D.norm();
    if (d1 == 0.0D)
      return (0.0F / 0.0F);
    double d2 = dotProduct(paramVector3D);
    double d3 = d1 * 0.9999D;
    if ((d2 < -d3) || (d2 > d3))
    {
      Vector3D localVector3D = crossProduct(paramVector3D);
      if (d2 >= 0.0D)
        return (float)Math.asin(localVector3D.norm() / d1);
      return (float)(3.141592653589793D - Math.asin(localVector3D.norm() / d1));
    }
    return (float)Math.acos(d2 / d1);
  }

  public float[] asArray()//向量转换为矩阵
  {
    float[] arrayOfFloat = new float[3];
    arrayOfFloat[0] = this.x;
    arrayOfFloat[1] = this.y;
    arrayOfFloat[2] = this.z;
    return arrayOfFloat;
  }

  public Vector3D crossProduct(Vector3D paramVector3D)//向量叉乘
  {
    float f1 = paramVector3D.getX();
    float f2 = paramVector3D.getY();
    float f3 = paramVector3D.getZ();
    return new Vector3D(f2 * this.z - f3 * this.y, f3 * this.x - f1 * this.z, f1 * this.y - f2 * this.x);
  }

  public float distanceFromLine(Vector3D paramVector3D1, Vector3D paramVector3D2)//点到直线（D1D2）的距离
  {
    return (float)(subtract(paramVector3D1).crossProduct(subtract(paramVector3D2)).norm() / paramVector3D2.subtract(paramVector3D1).norm());
  }

  public float dotProduct(Vector3D paramVector3D)//向量点乘
  {
    float f1 = paramVector3D.getX();
    float f2 = paramVector3D.getY();
    float f3 = paramVector3D.getZ();
    return f1 * this.x + f2 * this.y + f3 * this.z;
  }

  public float getX()
  {
    return this.x;
  }

  public float getY()
  {
    return this.y;
  }

  public float getZ()
  {
    return this.z;
  }

  public double norm()//向量求模
  {
    return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
  }

  public Vector3D normalize()//向量单位化
  {
    float f1 = (float)norm();
    if (f1 != 0.0F)
    {
      float f2 = 1.0F / f1;
      return new Vector3D(f2 * this.x, f2 * this.y, f2 * this.z);
    }
    return NaN;
  }

  public Vector3D scalarMultiply(float paramFloat)//向量与标量相乘
  {
    return new Vector3D(paramFloat * this.x, paramFloat * this.y, paramFloat * this.z);
  }

  public Vector3D subtract(Vector3D paramVector3D)//向量相减
  {
    float f1 = paramVector3D.getX();
    float f2 = paramVector3D.getY();
    float f3 = paramVector3D.getZ();
    return new Vector3D(this.x - f1, this.y - f2, this.z - f3);
  }

  public String toString()
  {
    return "Vector3D [x: " + this.x + ", y: " + this.y + ", z: " + this.z + "]";
  }
}