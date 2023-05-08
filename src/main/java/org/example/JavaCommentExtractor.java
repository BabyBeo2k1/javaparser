package org.example;

import com.github.javaparser.ParseException;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.opencsv.CSVWriter;


import javax.json.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaCommentExtractor {

    static JsonArrayBuilder outputJson = Json.createArrayBuilder();
    static int MIN_LINES = 1;//

    public static String extractRawComment(String comment){
        //extract target from javadoc
        String rawComment= comment.replaceAll("[^\\w\\s\\n@{}]+","");
        rawComment = rawComment.replaceAll("\\{[^\\}]*\\}", "");
        String res="";
        for (String line : rawComment.lines().toArray(String[]::new)) {
            line = line.strip();
            if (line.startsWith("@")){ // keep @return parameter, and delete other parameters
                if (line.startsWith("@return")){
                    line = line.replaceAll("@return", ". Return");
                    res +=line;
                    continue;
                } else if (line.startsWith("@param")){
                    line = line.replaceAll("@param", ". Given");
                    res +=line;
                    continue;
                }else {
                    continue;
                }
            }


            res += " " + line;
//            for (char c : line.toCharArray()) {
//                if (c < 127) {
//                    res+= " HUNGLOI"; // Non-English character found
//                }
//            }
        }
        return res.strip();
    }
    public static String removeCommentRegex(String code){
        return code.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)","");
    }

    public static String extractJavadocManually(String code){
        String patternStr = "(\\/\\*\\*)(.|\\n)+(\\*\\/)";
        Pattern pattern = Pattern.compile(patternStr);
        code = code.replaceAll("\r","");
        code = "/**\n" +
                " * Breaks a string into a list of pieces that will fit a specified width.\n" +
                " */\n" +
                "public static List<String> wrap(Font font, String str, int wrapWidth) {\n" +
                "    return Arrays.asList(wrapFormattedStringToWidth(font, str, wrapWidth).split(\"\\n\"));\n" +
                "}";
        Matcher matcher = pattern.matcher(code);
        if (matcher.find()){
            return matcher.group(2);
        }
        return "";
    }

    public static class CommentVisitor extends VoidVisitorAdapter<Void>{
        private String repo;
        public  CommentVisitor (String repo){
            this.repo=repo;
        }
        @Override
        public void visit( MethodDeclaration n, Void arg) {
            super.visit(n, arg);
//            System.out.println("New method: ");
//            System.out.println("New method: "+ n.removeComment().toString());
            Optional comment = n.getComment();
            Optional body = n.getBody();
            List<Comment> comments = n.getAllContainedComments();
            String rawBody="";
            if (body.isPresent()){
                BlockStmt bodyObject = (BlockStmt) body.get();
                rawBody = bodyObject.removeComment().toString().strip();
                //rawbody check num lines
            }
            // print excluded output

            if (comment.isPresent() && comment.get() instanceof JavadocComment){
                String title = String.format("name = %s", n.getName());
                String rawMethod = n.removeComment().toString();
                JavadocComment javadocComment = (JavadocComment) comment.get();
                String formattedComment = extractRawComment(javadocComment.toString());
                if (rawMethod.lines().count() > MIN_LINES && !formattedComment.isEmpty()) {
//                    System.out.println(title);
//                    System.out.println(removeCommentRegex(rawMethod));
//                    System.out.println(comment.get());
                    JsonObject data = Json.createObjectBuilder()
                            .add("source", removeCommentRegex(rawMethod))
                            .add("target", formattedComment)
                            .add("repo",this.repo)
                            .build();
                    outputJson.add(data);
                }
            }
            // check javadoc comment manually by regex
/*            String manualCheckJavadoc = extractJavadocManually(n.toString());
            if (!manualCheckJavadoc.equals("")){
                String rawMethod = n.removeComment().toString();
                JsonObject data = Json.createObjectBuilder()
                        .add("source", removeCommentRegex(rawMethod))
                        .add("target", manualCheckJavadoc)
                        .build();
                outputJson.add(data);
            }
*/
//            for (int i = 0; i < comments.size(); i++)
//            {
//                Comment comment = comments.get(i);
//                String title = String.format("name = %s", n.getName());
//                System.out.println(title);
//                System.out.println(comment);
//            }
        }
    }

    public static void main(String[] args) throws IOException, ParseProblemException {
        String absolutePath = new File(".").getAbsolutePath();
        absolutePath = absolutePath.substring(0, absolutePath.length() - 1);
        String filename = "src/data/EventListenerSupport.java";
//        try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
//            String line, lines = "";
//            while ((line = br.readLine()) != null)
//                lines += line;
//        }
        String basePath="/media/lqhung2001/Coding/LQH/KSTN/LAB_RISE/code_summary/crawl_1010/OneDrive_1_4-22-2023/";
        String baseoutPath="/media/lqhung2001/Coding/LQH/KSTN/LAB_RISE/code_summary/crawl_1010/OneDrive_1_4-22-2023/";
        ArrayList<String> dirPaths=new ArrayList<String>();
//        dirPaths.add("dromara/hertzbeat");
//        dirPaths.add("alibaba/fastjson2");
//        dirPaths.add("apitable/apitable");
//        dirPaths.add("aress31/burpgpt");
//        dirPaths.add("Automattic/pocket-casts-android");
//        dirPaths.add("CatVodTVOfficial/TVBoxOSC");
//        dirPaths.add("cozodb/cozo");
//        dirPaths.add("dromara/dynamic-tp");
//        dirPaths.add("Ehviewer-Overhauled/Ehviewer");
//        dirPaths.add("getcursor/cursor");
//        dirPaths.add("google/comprehensive-rust");
//        dirPaths.add("google/osv-scanner");
//        dirPaths.add("Grasscutters/Grasscutter");
//        dirPaths.add("hktalent/scan4all");
//        dirPaths.add("krahets/hello-algo");
//        dirPaths.add("mobile-dev-inc/maestro");
//        dirPaths.add("openblocks-dev/openblocks");
//        dirPaths.add("PrismLauncher/PrismLauncher");
//        dirPaths.add("PRQL/prql");
//        dirPaths.add("recloudstream/cloudstream");
//        dirPaths.add("reloadware/reloadium");
//        dirPaths.add("risingwavelabs/risingwave");
//        dirPaths.add("THUDM/CodeGeeX");
//        dirPaths.add("VueTubeApp/VueTube");
//        dirPaths.add("ydb-platform/ydb");
        dirPaths.add("twitter/the-algorithm");
        ArrayList<String> outextPaths=new ArrayList<String>();

//        outextPaths.add("hertzbeat");
//        outextPaths.add("fastjson2");
//        outextPaths.add("apitable");
//        outextPaths.add("burpgpt");
//        outextPaths.add("pocket-casts-android");
//        outextPaths.add("TVBoxOSC");
//        outextPaths.add("cozo");
//        outextPaths.add("dynamic-tp");
//        outextPaths.add("Ehviewer");
//        outextPaths.add("cursor");
//        outextPaths.add("comprehensive-rust");
//        outextPaths.add("osv-scanner");
//        outextPaths.add("Grasscutter");
//        outextPaths.add("scan4all");
//        outextPaths.add("hello-algo");
//        outextPaths.add("maestro");
//        outextPaths.add("openblocks");
//        outextPaths.add("PrismLauncher");
//        outextPaths.add("prql");
//        outextPaths.add("cloudstream");
//        outextPaths.add("reloadium");
//        outextPaths.add("risingwave");
//        outextPaths.add("CodeGeeX");
//        outextPaths.add("VueTube");
//        outextPaths.add("ydb");
        outextPaths.add("the-algorithm");
        String csv_name="out.csv";
        File csv_file= new File(csv_name);
        //String directoryPath = "/media/lqhung2001/New Volume/LQH/KSTN/LAB RISE/code summary/crawl_1010/OneDrive_1_4-22-2023/dromara/hertzbeat"; //change the directory path
//        directoryPath = "C:\\Users\\HUNG\\Documents\\hust\\rise\\java-parser\\src\\data\\tmp";

        for (int i=0;i<dirPaths.size();i++){
            String directoryPath= basePath+dirPaths.get(i);

            File directory = new File(directoryPath);
            File[] files = directory.listFiles();
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".java")) {
                    System.out.println("Parsing: " + file.getName());
                    try {
                        CompilationUnit cu = StaticJavaParser.parse(file);
                        CommentVisitor commentVisitor  = new CommentVisitor(outextPaths.get(i));
                        commentVisitor.visit(cu, null);
                    } catch (ParseProblemException | IOException e) {
                        System.out.println("Failed:" + e);
                        new RuntimeException(e);
                    }

                }
            }

        }
        String outputPath=baseoutPath +"tt.json";
        JsonArray finalOutput = outputJson.build();
        System.out.println(finalOutput.get(0));
        try (FileWriter fileWriter = new FileWriter(outputPath)) {
            // Create a JsonWriter object to write JSON-formatted data to the file
            JsonWriter jsonWriter = Json.createWriter(fileWriter);

            // Write the JSON data to the file
            jsonWriter.writeArray(finalOutput);

            // Flush and close the JsonWriter and FileWriter objects
            jsonWriter.close();
            fileWriter.close();

            System.out.println("Data written to file successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String filePath=baseoutPath  +"tt.csv";

        File file = new File(filePath);
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);

            // adding header to csv
            String[] header = { "source","target","repo"};
            writer.writeNext(header);
            for (JsonValue data: finalOutput){
                String[] write_line={((JsonObject) data).getString("source"),((JsonObject) data).getString("target"),((JsonObject) data).getString("repo")};
                writer.writeNext(write_line);
            }
            // add data to csv
//            String[] data1 = { "Aman", "10", "620" };
//            writer.writeNext(data1);
//            String[] data2 = { "Suraj", "10", "630" };
//            writer.writeNext(data2);

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
