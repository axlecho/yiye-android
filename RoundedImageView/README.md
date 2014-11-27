RoundedImageView
================

A fast ImageView (and Drawable) that supports rounded corners (and ovals or circles) based on the original [example from Romain Guy](http://www.curious-creature.org/2012/12/11/android-recipe-1-image-with-rounded-corners/)

![RoundedImageView screenshot](https://raw.github.com/makeramen/RoundedImageView/master/screenshot.png)
![RoundedImageView screenshot with ovals](https://raw.github.com/makeramen/RoundedImageView/master/screenshot-oval.png)

There are many ways to create rounded corners in android, but this is the fastest and best one that I know of because it:
* does **not** create a copy of the original bitmap
* does **not** use a clipPath which is not hardware accelerated and not anti-aliased.
* does **not** use setXfermode to clip the bitmap and draw twice to the canvas.

If you know of a better method, let me know and I'll implement it!

Also has proper support for:
* Borders (with Colors and ColorStateLists)
* Ovals and Circles
* All `ScaleType`s
  * Borders are drawn at view edge, not bitmap edge.
  * Except on edges where the bitmap is smaller than the view
  * Borders are **not** scaled up/down with the image (correct width and radius are maintained)
* Anti-aliasing
* Transparent backgrounds
* Hardware acceleration
* Support for LayerDrawables (including TransitionDrawables)


Gradle
----
RoundedImageView is available in [Maven Central](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.makeramen%22%20AND%20a%3A%22roundedimageview%22).

Add the following to your `build.gradle` to use:
```
repositories {
    mavenCentral()
}

dependencies {
    compile 'com.makeramen:roundedimageview:1.3.0'
}
```

Usage
----
Define in xml:

```xml
<com.makeramen.RoundedImageView
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:id="@+id/imageView1"
        android:src="@drawable/photo1"
        android:scaleType="centerCrop"
        app:corner_radius="30dip"
        app:border_width="2dip"
        app:border_color="#333333"
        app:mutate_background="true"
        app:oval="true" />
```

Or in code:

```java
RoundedImageView iv = new RoundedImageView(context);
iv.setScaleType(ScaleType.CENTER_CROP);
iv.setCornerRadius(10);
iv.setBorderWidth(2);
iv.setBorderColor(Color.DKGRAY);
iv.setMutateBackground(true);
iv.setImageDrawable(drawable);
iv.setBackground(backgroundDrawable);
iv.setOval(true);
```

Or make a Transformation for Picasso:

```java
Transformation transformation = new RoundedTransformationBuilder()
          .borderColor(Color.BLACK)
          .borderWidthDp(3)
          .cornerRadiusDp(30)
          .oval(false)
          .build();

Picasso.with(context)
    .load(url)
    .fit()
    .transform(transformation)
    .into(imageView);
```

ChangeLog
----------

* **1.3.0**
    * A new `RoundedTransformationBuilder` to help build Picasso `Transformation`s
    * slight API changes:
        * all dimensions are now set at `float`s. `int`s will be interpreted as dimension resource IDs
        * `round_background` is now `mutate_background`, and a `RoundedDrawable` will no longer be created for the background if `mutate_background` is false.

* **1.2.4**
    * add basic support for ColorDrawable (and other drawables with -1 intrinsic dimens)
    * implementation of the above is known to be buggy in many cases, pull requests welcome

* **1.2.3**
    * added rudimentary support for `setImageUri`. Performance of the function is probably poor and users should be cautious when using it.

* **1.2.2**
    * fix for incorrect radius on the image when there is a border
    * add a `toBitmap()` function for easier Picasso and Ion compatibility

* **1.2.1**
    * default scaleType now FIT_CENTER (and never null) to match Android ([#27](https://github.com/vinc3m1/RoundedImageView/issues/27))

* **1.2.0**
    * add `setDither` and `setFilterBitmap` method support on RoundedDrawable for tuning bitmap scaling quality
    * improved performance for `setImageResource`
    * RoundedDrawable constructor is now public
    * Fixed bug where artifact was downloading `aar.asc` file instead of aar. You no longer need to have `@aar` specified in the dependency

* **1.1.0**
    * LayerDrawable support (needs testing!)
    * Refactored api to support chaining and remove repetitive code

* **1.0.0**
    * Initial release to maven central
    * Programmatically setting attributes with TransitionDrawables not supported.



[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/vinc3m1/roundedimageview/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

