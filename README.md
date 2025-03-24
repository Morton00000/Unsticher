# Unsticher
An image unsticher originaly made for Minecraft.

## Notes
The program is expecting an image in .png format and a text file with extension .config of the same name.

There can be multiple pairs of these files.

It is expecting them to be in the same folder as the .jar file.

The .png image is expected to be a 16 x 16 set of images.

## Config
The config file has several setup options.

The first line should be "output=/".

You can add a folder path to this but it must end in "/".


Next are the icon locations.

This is done with the row then collom seperate with a "." followed by an "=" and then the filename without an extension.

The file names can have spaces.

Example:

3.5=Testing


You can comment our lines by starting them with a "*".

Example:

*3.5=Testing


You can setup an array of images to be exported as one image with "-".

This will be noted as the upper left location and the lower right location seperated by a comma.

Example:

-3.5,5.6=Testing Long


## Output Notes
The config name location can also be a path then filename to have images go to different locations.

This filepath will be added to the master output path noted on the first line.

Example:

5.6=icons/Test
