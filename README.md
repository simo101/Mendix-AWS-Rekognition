# Mendix AWS Rekoginition Template
Welcome to the Mendix AWS Rekognition template. This template has been designed to help you get started with using Mendix and AWS Rekognition. It contains all the required modules to make it easy for you to build an app connected to AWS Rekognition. Once built the app will allow you to take a photo on your mobile phone or laptop, upload it to AWS Rekognition, and view the results of the Rekognition analysis. The template contains a start and complete module so that you can either decide to use the final solution or build your way up to the solution.

<b>This template already assumes that you have some knowledge of AWS, a AWS account, and a Mendix account</b>

You can signup for a free Mendix account for free here: [Sign Up for Free](https://signup.mendix.com/link/signup/?source=none&medium=aws-demo)

Gain free hands on experience with AWS here: [Sign Up](https://aws.amazon.com/free/?all-free-tier&all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc&awsf.Free%20Tier%20Types=*all&awsf.Free%20Tier%20Categories=*all)

- [Setup](#setup)
- [The Mendix Build](#the-mendix-build)
    - [Building the domain model](#building-the-domain-model)
    - [Building the User interface](#building-the-user-interface)
    - [Building the logic](#building-the-logic)
- [The AWS Build](#aws-build)
    - [AWS S3 Dataset](#aws-s3-dataset)
    - [Rekognition Training](#rekognition-training)
        - [Create a project](#create-a-project-console)
        - [Create a dataset](#create-dataset)
        - [Label Images](#label-images)
        - [Train Model](#train-model)
        - [Evaluate](#evaluate)
        - [Use Model](#use-model)


## Setup
In order to connect to AWS Rekognition it's important that you set up a number of constants. These constants are environment variables needed to make sure that the app can connect to the right AWS service using your AWS credentials.

### Setting your AWS Access and Secret Keys
In order to authenticate with AWS services, it's important that requests are signed using an AWS access and secret key. In Mendix this is done using the Sig4 process. Inside this application, we have already included a module to help with this process.
1. Create access and secret key pair on AWS with access to Rekognition service
2. Copy each of the keys
3. With the application open inside Studio Pro expand the Marketplace modules folder item in the app explorer.
4. Then expand the AWS_Sig4 module.
5. Finally, double click on the Access Key ID and Secret Key then paste the values into each.

<img src="readme-img/app-explorer.jpg"/>

### Setting up the Rekognition constants
The Rekognition module has two constants that need to be set to ensure that the APIs can communicate with the pre-built Rekognition model. These are:
1. AWS_HostPattern - This should be the URL of the endpoint that the API is calling, this will be different depending on the AWS region used for Rekognition. The endpoint URLs can be found here: https://docs.aws.amazon.com/general/latest/gr/rekognition.html
2. AWS_Region - This should be set to the region where the Rekognition AI model is deployed. The regions can be found here: https://docs.aws.amazon.com/general/latest/gr/rekognition.html

These constants can be found inside the AWS_Rekognition module in the marketplace folder. Then open up the constants folder:

<img src="readme-img/app-explorer-rekognition.jpg"/>

Once you have these constants set up you're ready to begin your build.

## The Mendix Build

### Building the domain model
The first step with many Mendix projects is to start with building the data structure. Data is modeled in Mendix using domain models. Each Module in Mendix contains a domain model where you can model the entities, associations and attributes. Follow the below steps to build out the right structure. These instructions assume that you have already pre-installed Mendix Studio Pro 9.12.2+.

1. Using the App Explorer on the left-hand side open up the module "MxRekognitionDemo_Start".
2. Double click on the "Domain model" 
3. Drag an entity from the Toolbox onto the Canvas. The toolbox can be often found on the right-hand side.

<img src="readme-img/mx-build-entity.jpg"/>

4. Double click on the Entity to open the dialog box.
5. Change the name to Picture.

<img src="readme-img/mx-build-picture-entity.jpg"/>

6. Click the "Select" button next to "Generalization".
7. In the Search field type "Image".

<img src="readme-img/mx-build-generalization.jpg"/>

8. Double click on the Image entity.
9. Click the "Ok" button to close the dialog.
10. Drag another entity onto the workbench.

<img src="readme-img/mx-build-label-entity.jpg"/>

11. Double click on this entity and rename it to Label.
12. Under the "Attributes" tab click the "New" button.
13. Name your first attribute "Name".
14. Click "Ok" to close the dialog.
15. Add another Attribute and call this one "Confidence".
16. Under the Data Type Dropdown select "Decimal".

<img src="readme-img/mx-build-label-properties.jpg"/>

17. Click "Ok" and then "Ok" to close both dialogs.
18. Associate the two entities by dragging the arrow from Label to Picture. This will create a relationship between these two entities.

<img src="readme-img/mx-build-label-associate.jpg"/>

### Building the User Interface
1. Open up the folder "Pages" inside the "MxRekognitionDemo_Start" module.
2. Double click on "Home_Start" to open up.
3. From the right-hand side open up the "Toolbox" and then "Building Blocks".

<img src="readme-img/mx-build-page-build.jpg"/>

4. Drag the Block labeled "Label Block" to the bottom empty space.
5. Drag the other building block "Picture Block" to the space above.

<img src="readme-img/mx-build-page-build-blocks.jpg"/>

6. Next, we need to connect these up to our Picture object. Click on the widgets tab on the right-hand side.
7. Drag on a "Data view" widget onto the page at the top.

<img src="readme-img/mx-build-page-dataview.jpg"/>

8. Double click on the widget to open up the properties dialog.
9. Under "Data source" select "Nanoflow"
10. Click the "Select" button.

<img src="readme-img/mx-build-page-datasource.jpg"/>

11. Click the "New" button at the bottom of the popup.
12. Give the Nanoflow a name like "DSO_NewPicture".

<img src="readme-img/mx-build-page-get-picture.jpg"/>

13. Click the "Show" button to open up the nanoflow and close the dialog.

<img src="readme-img/mx-build-page-nanoflow.jpg"/>

14. Using the Toolbox drag on a "Create Object" action.

<img src="readme-img/mx-build-page-create-action.jpg"/>

15. Double click on the action and set the entity type to our new "Picture" entity.

<img src="readme-img/mx-build-page-select-entity.jpg"/>

16. Double click on the "Endpoint" represented by a red dot. 
17. Configure it to return an "Object" 

<img src="readme-img/mx-build-page-nanoflow-return.jpg"/>

18. Set the value to the newly created Object.

<img src="readme-img/mx-build-page-nanoflow-set-return.jpg"/>

19. Open up the "Home_Start" page again.
20. Drag the "Layout" into the Dataview.
21. Double click on the Picture control and connect it to the "Picture" entity.
22. Double click on the "List view" and connect it to the associated entity "Label"

<img src="readme-img/mx-build-page-listview-association.jpg"/>

23. Configure the left parameter in the ListView by double-clicking on the text item, then use then connect Parameter {1} up to "Name".

<img src="readme-img/mx-build-page-setting-labels.jpg"/>

24. Configure the right parameter in the ListView by double-clicking on the text item, then use then connect Parameter {1} up to "Confidence".

<img src="readme-img/mx-build-page-setting-labels-confidence.jpg"/>

### Building the logic
Logic in Mendix is defined using Microflows for server-side logic and Nanoflows for client-side. Both of these concepts use the same modeling paradigm. Allowing you to define logic using actions, decisions and loops.

To perform the logic needed we'll create a Nanoflow which will open up the camera, save the picture, process it by Rekognition, and save the results. Here are the steps:

1. Right-click on the "Take a picture" button and click "Edit on click action".
2. Select from the dropdown "Call a Nanoflow".
3. Click the "New" button.
4. Enter the name "ACT_TakePicture" and click "OK".

<img src="readme-img/mx-build-page-edit-action.jpg"/>

5. Open up the newly built Nanoflow.
6. Drag and Drop from the Toolbox a "Take Picture" action.

<img src="readme-img/mx-build-logic-take-picture.jpg"/>

7. Configure the parameters as follows:
    - Picture = NewPicture
    - Show Confirmation Screen = false
    - Picture Quality = low
    - Maximum width = empty
    - Maximum height = empty

<img src="readme-img/mx-build-logic-take-picture-options.jpg"/>

8. From the Toolbox drag the detect custom labels action and configure as follows:
    - ProjectARN = Your Rekognition ARN
    - Image = NewPicture
    - MaxResults = 10
    - MinConfidence = 0
    - AWS_Region = your region

<img src="readme-img/mx-build-logic-rekognition.jpg"/>

9. Add a Loop activity to the microflow and connect it to the CustomLabel List

<img src="readme-img/mx-build-logic-loop.jpg"/>

10. Inside the loop drag a retrieve action to retrieve the BoundingBox.
11. Connect up the retrieve action by double-clicking on the action, selecting "By association", clicking "Select", and selecting the Bounding Box association.

<img src="readme-img/mx-build-logic-retrieve-action.jpg"/>

12. Drag on a "Create" action into the loop and draw a line from the bounding box to the "Create" action.

<img src="readme-img/mx-build-logic-create-action.jpg"/>

13. Configure the activity by selecting the Entity "Label".
14. Set 3 Members to the following:
    - Label_Picture = $NewPicture
    - Confidence = $IteratorCustomLabel/Confidence
    - Name = $IteratorCustomLabel/Name

<img src="readme-img/mx-build-logic-create-configure.jpg"/>

15. Finally add a bounding box activity and configure as follows:
    - Class name = 'img-container'
    - Bounding box = $BoundingBox
    - Custom label = $IteratorCustomLabel
    - High Confidence Threshold = 80
    - Medium Confidence Threshold = 50

<img src="readme-img/mx-build-logic-bounding-box.jpg"/>

16. We're now complete so we need to run the project locally. Click the Green play Icon on the top right.

<img src="readme-img/mx-build-run-app.jpg"/>

17. Open up your app locally on your laptop by going to http://localhost:8080. If you wish to try it on your phone you'll need to create a secure tunnel using something like [ngrok](https://ngrok.com/) to ensure that the camera on your phone can be accessed. Mobile browsers only allow you to access the camera over a secure connection using https. 

## AWS Build
### AWS S3 Dataset
1.	Sign in to the AWS Management Console and open the Amazon S3 console at https://console.aws.amazon.com/s3/.
2.	Choose Create bucket.
The Create bucket wizard opens.
3.	In Bucket name, enter a DNS-compliant name for your bucket. For example “mendixcars-yourname”
4.	In Region, choose the AWS Region where you want the bucket to reside.
Choose a Region close to you to minimize latency and costs and address regulatory requirements.
5.	Leave other settings as default, scroll down and click Create bucket button.
6.	Select a new bucket and create folders in the bucket by clicking Create folder button.
Create a folder: cars. Inside it: mercedes, bmw, scratch and upload images

<img src="readme-img/s3-bucket.png"/>

7. Depending on the number of images you can install AWS CLI and use S3 sync command.

<img src="readme-img/console.png"/>

Example: 

```aws s3 sync . s3://mybucket```

### Rekognition Training
<img src="readme-img/rekognition-steps.png"/>

With Amazon Rekognition Custom Labels, you can identify the objects and scenes in images that are specific to your business needs. For example, you can find your logo in social media posts, identify your products on store shelves, classify machine parts in an assembly line, distinguish healthy and infected plants, or detect animated characters in videos.

No machine learning expertise is required to build your custom model. Rekognition Custom Labels includes AutoML capabilities that take care of the machine learning for you. Once the training images are provided, Rekognition Custom Labels can automatically load and inspect the data, select the right machine learning algorithms, train a model, and provide model performance metrics.

Rekognition Custom Labels builds off of Rekognition’s existing capabilities, which are already trained on tens of millions of images across many categories. Instead of thousands of images, you simply need to upload a small set of training images (typically a few hundred images or less) that are specific to your use case into our easy-to-use console. If your images are already labeled, Rekognition can begin training in just a few clicks. If not, you can label them directly within Rekognition’s labeling interface, or use Amazon SageMaker Ground Truth to label them for you. Once Rekognition begins training from your image set, it can produce a custom image analysis model for you in just a few hours. Behind the scenes, Rekognition Custom Labels automatically loads and inspects the training data, selects the right machine learning algorithms, trains a model, and provides model performance metrics. You can then use your custom model via the Rekognition Custom Labels API and integrate it into your applications.

In this example, we will train to analyze car makers and damages.

#### Create a project (console)
1.	Sign in to the AWS Management Console and open the Amazon Rekognition console at https://console.aws.amazon.com/rekognition/
2.	In the left pane, choose Use Custom Labels. The Amazon Rekognition Custom Labels landing page is shown.
3.	The Amazon Rekognition Custom Labels landing page, choose to Get started. In the left pane, Choose Projects. 
4.	Choose a Region close to you to minimize latency and costs and address regulatory requirements.
5.	Choose Create Project.
6.	In Project name, enter a name for your project. For example “mendixcars”
7.	Choose Create project to create your project.

#### Create Dataset

1.	Choose Create dataset. The Create dataset page is shown.
2.	In Starting configuration, choose either Start with a single dataset 

<img src="readme-img/rekognition-dataset.png">

3.	Choose Import images from Amazon S3 bucket.
4.	In S3 URI, enter the Amazon S3 bucket location and folder path. Select a parent “cars” folder that contains folders: bmw, mercedes, scratch and click Copy S3 URI.

<img src="readme-img/rekognition-objects.png">

5.	Choose Automatically attach labels to images based on the folder.
6.	Choose Create Datasets. The datasets page for your project opens.

<img src="readme-img/rekognition-explore.png">

7.	Scroll down and copy the policy provided.  In a new tab, open the S3 console and select your bucket with images. 

<img src="readme-img/rekognition-policy.png">

8.	On “Permissions” tab scroll down to “Bucket policy” and click edit and paste the policy. Click Save changes to save policy update.

<img src="readme-img/rekognition-permissions.png">

<img src="readme-img/rekognition-permissions2.png">

9.	Go back to Rekognition configuration, click “Create Dataset” in Rekognition console. Depending on the number of images, it might take a few minutes to create a dataset.

#### Label Images
1.	Choose Start labeling to enter labeling mode. Select a first label. 
Note: Do car models first (bmw, mercedes) and then do defects (scratches).
2.	In the image gallery, select one or more images that you want to add labels to. You can only select images on a single page at a time. To select a contiguous range of images on a page:
3.	Select the first image in the range.
4.	Press and hold the shift key.
5.	Select the last image range. The images between the first and second image are also selected.
6.	Release the shift key.
7.	Choose Assign Labels.

<img src="readme-img/rekognition-labelling.png">

8. Click Draw bounding boxes and mark scratches as well as cars on the images

<img src="readme-img/rekognition-labelling2.png">

9.	Repeat labeling until every image is annotated with the required labels. 
10.	Choose Save changes to save your changes.
11.	Choose Exit to exit labeling mode.

#### Train Model
1.  On the Project page, choose Train model.

<img src="readme-img/rekognition-train-model.png">

2.  Keep default settings and click Train model. Depending on a number of images it will take from 30 minutes to 24 hours. 
Note: 200 images took about 40 minutes.

<img src="readme-img/rekognition-train-confirmation.png">

<img src="readme-img/rekognition-train-process.png">

#### Evaluate
1.	In the Models section of the project page, you can check the current status in the Model Status column, where the training's in progress.

<img src="readme-img/rekognition-evaluate.png">

After your model is trained, Amazon Rekognition Custom Labels provides the following metrics as a summary of the training results and as metrics for each label: Precision, Recall, F1.

2.  If you are interested in how your model performed on test images Choose View test results to see the results for individual test images. For more information, see [Metrics for evaluating your model](https://docs.aws.amazon.com/rekognition/latest/customlabels-dg/im-metrics-use.html).

<img src="readme-img/rekognition-evaluate-2.png">

<img src="readme-img/rekognition-evaluate-3.png">

#### Use Model
1.	In the Start or stop model section select the number of inference units that you want to use. For more information, see [Running a trained Amazon Rekognition Custom Labels model](https://docs.aws.amazon.com/rekognition/latest/customlabels-dg/running-model.html).
2.	Choose Start. In the Start model dialog box, choose Start.
3.	In the Model section, check the status of the model. When the model status is RUNNING, you can use the model to analyze images. 

<img src="readme-img/rekognition-use-model.png">

4. You can test your model using AWS CLI command.

```aws rekognition detect-custom-labels --project-version-arn "your model arn" --image "S3Object={Bucket=mendixcars, Name=car2.jpg}" --region us-west-2```

Replace the information in yellow with details of your model and a bucket containing new images for analysis.