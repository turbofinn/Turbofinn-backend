#!/bin/sh

TIME_NOW_ISO=$(date -u +"%Y-%m-%dT%H:%M:%SZ")
LAYER_PACKAGE_NAME="TF-Lambda-Layer-${TIME_NOW_ISO}.zip"

# PROD CONFIG
SERVER_NAME="PROD"
LAYER_NAME="TF-Lambda-Layer"
WEB_CODE_S3_BUCKET_NAME="lambda-layer-turbofinn"
AWS_PROFILE_NAME="turbofinn"
AWS_REGION="us-east-1"

# Step 1: Change directory
cd /Users/gauravsingh/Desktop/Turbofinn-backend/src/main/java/lib/ || exit

# Verify the current directory
echo "Current directory: $(pwd)"

# Step 2: Zip the contents of the lib directory
echo "Creating zip file..."
zip -r "${LAYER_PACKAGE_NAME}" .

# Verify the zip file is created
if [ ! -f "${LAYER_PACKAGE_NAME}" ]; then
    echo "Zip file ${LAYER_PACKAGE_NAME} not created."
    exit 1
fi

# Step 3: Upload to S3
echo "Created layer package, Uploading to S3..."
aws s3 cp "${LAYER_PACKAGE_NAME}" "s3://${WEB_CODE_S3_BUCKET_NAME}/" --profile ${AWS_PROFILE_NAME} --region ${AWS_REGION}

# Verify upload to S3
if [ $? -ne 0 ]; then
    echo "Failed to upload to S3."
    exit 1
fi

# Step 4: Publish the layer
echo "Uploaded to S3, publishing now..."
/usr/local/bin/aws lambda publish-layer-version --profile ${AWS_PROFILE_NAME} --layer-name ${LAYER_NAME} \
    --description "Update from script : ${TIME_NOW_ISO}" \
    --content S3Bucket=${WEB_CODE_S3_BUCKET_NAME},S3Key="${LAYER_PACKAGE_NAME}" \
    --compatible-runtimes java21 --region ${AWS_REGION}

if [ $? -ne 0 ]; then
    echo "Failed to publish layer version."
    exit 1
fi

echo "Layer Updated ${SERVER_NAME}"
