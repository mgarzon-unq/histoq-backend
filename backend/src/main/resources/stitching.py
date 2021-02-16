def stitchImageFiles(filePaths, outputFilePath):

    ##################################################
    # TO-DO: implement your own stitching algorithm  #
    ##################################################

    ##################################################
    # Dummy implementation
    with open(outputFilePath, 'w') as file_handler:
        for item in filePaths:
            file_handler.write("{}\n".format(item))

