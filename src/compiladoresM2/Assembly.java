package compiladoresM2;

import java.util.ArrayList;
import java.util.List;




public class Assembly {
    public List<String> dataArray = new ArrayList<>();
    public List<String> textArray = new ArrayList<>();
    private String tempAssembly[] = {"1000","1001","1002"};

    public void storeEL(Assembly assembly, String elem){
        assembly.textArray.add("        STO     " + elem + "\n");

    }

    public void subTempFor(Assembly assembly){
        assembly.textArray.add("        SUB     " + tempAssembly[0] + "\n");
    }
    public void addTempFor(Assembly assembly){
        assembly.textArray.add("        ADD     " + tempAssembly[1] + "\n");
    }

    public void multiVecOnBranch(Assembly assembly, String op, boolean isLeft){
        if(op.equals("+") && isLeft){
            assembly.textArray.add("        LD     " + tempAssembly[0] + "\n");
            assembly.textArray.add("        ADD     " + tempAssembly[1] + "\n");
        }else if (!op.equals("+") && isLeft){
            assembly.textArray.add("        LD     " + tempAssembly[0] + "\n");
            assembly.textArray.add("        SUB     " + tempAssembly[1] + "\n");
        }else if (op.equals("+") && !isLeft){
            assembly.textArray.add("        LD     " + tempAssembly[1] + "\n");
            assembly.textArray.add("        ADD     " + tempAssembly[2] + "\n");
        }else{
            assembly.textArray.add("        LD     " + tempAssembly[1] + "\n");
            assembly.textArray.add("        SUB     " + tempAssembly[2] + "\n");
        }
    }
    public void makeJump(Assembly assembly, int number){
        assembly.textArray.add("        JMP       " + number + "\n");
    }
    public void label(Assembly assembly,int topOfThePile)
    {
        assembly.textArray.add("  " + topOfThePile +":" +"\n");

    }
    public void funcLabel(Assembly assembly,String topOfThePile)
    {
        assembly.textArray.add("  " + topOfThePile +":" +"\n");

    }

    public void branchNaming(Assembly assembly, int name, String op){
        if(op.equals("==")) {
            assembly.textArray.add("        BNE       " + name + "\n");
        }
        if(op.equals(">=")){
            assembly.textArray.add("        BLT       " + name + "\n");
        }
        if(op.equals("<=")){
            assembly.textArray.add("        BGT       " + name + "\n");
        }
        if(op.equals(">")){
            assembly.textArray.add("        BLE       " + name + "\n");
        }
        if(op.equals("<")){
            assembly.textArray.add("        BGE       " + name + "\n");
        }
        if(op.equals("!=")){
            assembly.textArray.add("        BEQ       " + name + "\n");
        }
    }

    public void compareTemp(Assembly assembly){
        assembly.textArray.add("        LD       "+tempAssembly[0]+"\n");
        assembly.textArray.add("        SUB       "+tempAssembly[1]+"\n");

    }
    public void verifyBranchExp(Assembly assembly,String token){
        Boolean flag = Character.isDigit(token.charAt(0));

        if (flag) {
            assembly.textArray.add("        LDI       "+token+"\n");
        }
        else{
            assembly.textArray.add("        LD        "+token+"\n");
        }

    }
    public void storeTemp0(Assembly assembly){
        assembly.textArray.add("        STO        " + tempAssembly[0] + "\n");
    }
    public void storeTemp1(Assembly assembly){
        assembly.textArray.add("        STO        " + tempAssembly[1] + "\n");
    }
    public void storeTemp2(Assembly assembly){
        assembly.textArray.add("        STO        " + tempAssembly[2] + "\n");
    }

    public void oneIndexStart(Assembly assembly,String index){
        Boolean flag = Character.isDigit(index.charAt(0));
        if (flag) {
            assembly.textArray.add("        LDI       "+index+"\n");
        }
        else{
            assembly.textArray.add("        LD        "+index+"\n");
        }
    }


    public void indexOfVecExp(Assembly assembly, String lastToken,String currentToken,String tokenOp,String var){
        Boolean flag = Character.isDigit(currentToken.charAt(0));
        if (flag) {
            assembly.textArray.add("        LDI       "+currentToken+"\n");
        }
        else{
            assembly.textArray.add("        LD        "+currentToken+"\n");
        }
        Boolean flag2 = Character.isDigit(lastToken.charAt(0));
        if(flag2){
            if(tokenOp.equals("+")){assembly.textArray.add("        ADDI    " + lastToken + "\n");}
            if(tokenOp.equals("-")){assembly.textArray.add("        SUBI    " + lastToken + "\n");}

        }
        else{
            if(tokenOp.equals("+")){assembly.textArray.add("        ADD    " + lastToken + "\n");}
            if(tokenOp.equals("-")){assembly.textArray.add("        SUB    " + lastToken + "\n");}
        }
        if(tokenOp.equals(">>")){assembly.textArray.add("        SLL    " + lastToken + "\n");}
        if(tokenOp.equals("<<")){assembly.textArray.add("        SLR    " + lastToken + "\n");}
        if(tokenOp.equals("&")){assembly.textArray.add("        AND    " + lastToken + "\n");}
        if(tokenOp.equals("|")){assembly.textArray.add("        OR    " + lastToken + "\n");}
    }
    public void addToIndex(String token, String op,Assembly assembly){
        if(Main.func.listOfFunctions.get(Main.func.listOfFunctions.size()-1).parameterList.contains(token)){
            token = token +"_" +Main.func.listOfFunctions.get(Main.func.listOfFunctions.size()-1).name;
        }


        Boolean flag = Character.isDigit(token.charAt(0));
        if(flag){
            if(op.equals("+")){assembly.textArray.add("        ADDI    " + token + "\n");}
            if(op.equals("-")){assembly.textArray.add("        SUBI    " + token + "\n");}
        }
        else{
            if(op.equals("+")){assembly.textArray.add("        ADD    " + token + "\n");}
            if(op.equals("-")){assembly.textArray.add("        SUB    " + token + "\n");}
        }
        if(op.equals(">>")){assembly.textArray.add("        SLL    " + token + "\n");}
        if(op.equals("<<")){assembly.textArray.add("        SLR    " + token + "\n");}
        if(op.equals("&")){assembly.textArray.add("        AND    " + token + "\n");}
        if(op.equals("|")){assembly.textArray.add("        OR    " + token + "\n");}
    }
    public void endIndex(String leftElement, Assembly assembly){
        assembly.textArray.add("        STO       $indr"+"\n");
        assembly.textArray.add("        LDV       "+leftElement+"\n");

    }

    public void endIndexCin(String leftElement, Assembly assembly){
        assembly.textArray.add("        STO       $indr"+"\n");
        assembly.textArray.add("        LD       $in_port"+"\n");
        assembly.textArray.add("        STOV       "+leftElement+"\n");

    }
    public void endIndexMonoCout(String leftElement, Assembly assembly){
        assembly.textArray.add("        STO       $indr"+"\n");
        assembly.textArray.add("        LDV       "+leftElement+"\n");
        assembly.textArray.add("        STO       "+"$out_port"+"\n");

    }

    public void writeIfElIsVec(Assembly assembly, String lastToken,String currentToken ){
        Boolean flag = Character.isDigit(currentToken.charAt(0));
        if (flag) {
            assembly.textArray.add("        LDI       "+currentToken+"\n");
        }
        else{
            assembly.textArray.add("        LD        "+currentToken+"\n");
        }
        assembly.textArray.add("        STO       $indr"+"\n");
        assembly.textArray.add("        LDV       "+lastToken+"\n");
        assembly.textArray.add("        STO       $out_port"+"\n");

    }
    public void readIfElIsVec(Assembly assembly, String lastToken,String currentToken ){
        Boolean flag = Character.isDigit(currentToken.charAt(0));
        if (flag) {
            assembly.textArray.add("        LDI       "+currentToken+"\n");
        }
        else{
            assembly.textArray.add("        LD        "+currentToken+"\n");
        }
        assembly.textArray.add("        STO       $indr"+"\n");
        assembly.textArray.add("        LD        $in_port"+"\n");
        assembly.textArray.add("        STOV      "+lastToken+"\n");
    }


    public void readVarOrInt(Assembly assembly, String token){
        Boolean flag = Character.isDigit(token.charAt(0));
        if (flag) {
            assembly.textArray.add("        LDI   $in_port\n");
        }
        else{
            assembly.textArray.add("        LD   $in_port\n");
        }
        assembly.textArray.add("        STO  " +token+"\n");
    }
    public void writeVarOrInt(Assembly assembly, String  token){
        Boolean flag = Character.isDigit(token.charAt(0));
        if(flag){
            assembly.textArray.add("        LDI  " +token+"\n");
        }
        else{
            assembly.textArray.add("        LD  " +token+"\n");
        }
        assembly.textArray.add("        STO   $out_port\n");
    }
    public void storeCout(Assembly assembly){
        assembly.textArray.add("        STO   $out_port\n");
    }

    public void simpleVectReceivesVarOrInt(String token,String indexLeft, Assembly assembly, String elementOnTheLeftSideOfAttr){
        Boolean flag = Character.isDigit(indexLeft.charAt(0));
        if(flag){
            assembly.textArray.add("        LDI  " +indexLeft+"\n");
        }
        else{
            assembly.textArray.add("        LD   " +indexLeft+"\n");
        }

        assembly.textArray.add("        STO  " +tempAssembly[0]+"\n");
        Boolean tokenFlag = Character.isDigit(token.charAt(0));
        if(tokenFlag){
            assembly.textArray.add("        LDI  " +token+"\n");
        }
        else{
            assembly.textArray.add("        LD   " +token+"\n");
        }
        assembly.textArray.add("        STO  " +tempAssembly[1]+"\n");
        assembly.textArray.add("        LD   " +tempAssembly[0]+"\n");
        assembly.textArray.add("        STO  " +"$indr"+"\n");
        assembly.textArray.add("        LD   " +tempAssembly[1]+"\n");
    }

    public void simpleVecReceivesVec(String token,String indexLeft, Assembly assembly, String elementOnTheLeftSideOfAttr, String indexRight){
        System.out.println("simpleVecintVecIntAttr");
        Boolean indexFlag = Character.isDigit(indexLeft.charAt(0));
        if(indexFlag){
            assembly.textArray.add("        LDI  " +indexLeft+"\n");
        }
        else{
            assembly.textArray.add("        LD   " +indexLeft+"\n");
        }
        assembly.textArray.add("        STO  " +tempAssembly[0]+"\n");
        Boolean indexFlagRight = Character.isDigit(indexRight.charAt(0));
        if(indexFlagRight){
            assembly.textArray.add("        LDI   " +indexRight+"\n");
        }
        else{
            assembly.textArray.add("        LD    " +indexRight+"\n");
        }
        assembly.textArray.add("        STO   " +"$indr"+"\n");
        assembly.textArray.add("        LDV   " +token+"\n");
        assembly.textArray.add("        STO   " +tempAssembly[1]+"\n");
        assembly.textArray.add("        LD   " +tempAssembly[0]+"\n");
        assembly.textArray.add("        STO   " +"$indr"+"\n");
        assembly.textArray.add("        LD   " +tempAssembly[1]+"\n");
    }
    public void simpleVarReceivesVect(String token, Assembly assembly, String elementOnTheLeftSideOfAttr, String indexRight){
        System.out.println("simpleVectToVar");
        assembly.textArray.add("        LDI  " +indexRight+"\n");
        assembly.textArray.add("        STO   " +"$indr"+"\n");
        assembly.textArray.add("        LDV   " +token+"\n");
    }

    public void simpleVarReceivesIntOrVar(String token, Assembly assembly){
        System.out.println("simpleImmediateAttr");

        Boolean flag = Character.isDigit(token.charAt(0));
        if (flag) {
            assembly.textArray.add("        LDI  " +token+"\n");
        }
        else{
            assembly.textArray.add("        LD   " +token+"\n");
        }
        //assembly.textArray.add("        STO  " +elementOnTheLeftSideOfAttr+"\n");
    }



    public void addTo(String token2,String tokenOp, Assembly assembly){
        Boolean flag = Character.isDigit(token2.charAt(0));
        if(flag){
            if(tokenOp.equals("+")) {assembly.textArray.add("        ADDI   " + token2 + "\n");}
            else{assembly.textArray.add("        SUBI   " + token2 + "\n");}
        }
        else{
            if(tokenOp.equals("+")) {assembly.textArray.add("        ADD    " + token2 + "\n");}
            else{assembly.textArray.add("        SUB    " + token2 + "\n");}
        }
        if(tokenOp.equals(">>")){assembly.textArray.add("        SLL    " + token2 + "\n");}
        if(tokenOp.equals("<<")){assembly.textArray.add("        SLR    " + token2 + "\n");}
        if(tokenOp.equals("&")){assembly.textArray.add("        AND    " + token2 + "\n");}
        if(tokenOp.equals("|")){assembly.textArray.add("        OR    " + token2 + "\n");}
    }

    public void endIndexLeftVec(Assembly assembly){
        assembly.textArray.add("        STO     " + tempAssembly[1] + "\n");
        assembly.textArray.add("        LD     " + tempAssembly[0] + "\n");
        assembly.textArray.add("        STO     " + "$indr"+ "\n");
        assembly.textArray.add("        LD     " + tempAssembly[1] + "\n");
    }


    public void sumOrRedVecsLeftVec(Assembly assembly, String op, boolean isTheLastOne){
        if(isTheLastOne){
            if(op.equals("+")){
                assembly.textArray.add("        LD     " + tempAssembly[1] + "\n");
                assembly.textArray.add("        ADD     " + tempAssembly[2] + "\n");

            }else{
                assembly.textArray.add("        LD     " + tempAssembly[1] + "\n");
                assembly.textArray.add("        SUB     " + tempAssembly[2] + "\n");

            }
        }else{
            if(op.equals("+")){
                assembly.textArray.add("        LD     " + tempAssembly[1] + "\n");
                assembly.textArray.add("        ADD     " + tempAssembly[2] + "\n");
                assembly.textArray.add("        STO     " + tempAssembly[1] + "\n");
            }else{
                assembly.textArray.add("        LD     " + tempAssembly[1] + "\n");
                assembly.textArray.add("        SUB     " + tempAssembly[2] + "\n");
                assembly.textArray.add("        STO     " + tempAssembly[1] + "\n");
            }
        }
    }
    public void sumOrRedOfVecs(Assembly assembly, String op, boolean isTheLastOne){
        if(isTheLastOne){
            if(op.equals("+")){
                assembly.textArray.add("        LD     " + tempAssembly[0] + "\n");
                assembly.textArray.add("        ADD     " + tempAssembly[1] + "\n");
            }else{
                assembly.textArray.add("        LD     " + tempAssembly[0] + "\n");
                assembly.textArray.add("        SUB     " + tempAssembly[1] + "\n");
            }
        }else{
            if(op.equals("+")){
                assembly.textArray.add("        LD     " + tempAssembly[0] + "\n");
                assembly.textArray.add("        ADD     " + tempAssembly[1] + "\n");
                assembly.textArray.add("        STO     " + tempAssembly[0] + "\n");
            }else{
                assembly.textArray.add("        LD     " + tempAssembly[0] + "\n");
                assembly.textArray.add("        SUB     " + tempAssembly[1] + "\n");
                assembly.textArray.add("        STO     " + tempAssembly[0] + "\n");
            }
        }


    }
    public  void store(String elementOnTheLeft, Assembly assembly, boolean isVec){

        if(!isVec) {
            assembly.textArray.add("        STO  " + elementOnTheLeft + "\n");
        }
        else{
            assembly.textArray.add("        STOV " + elementOnTheLeft + "\n");
        }
    }
    public  void store(String elementOnTheLeft, Assembly assembly, boolean isVec, Function func){

        if(!isVec) {
            assembly.textArray.add("        STO  " + elementOnTheLeft+"_"+func.name + "\n");
        }
        else{
            assembly.textArray.add("        STOV " + elementOnTheLeft+"_"+func.name + "\n");
        }
    }
    public void call(String functionName, Assembly assembly){
        assembly.textArray.add("        CALL  " + functionName+ "\n");

    }


    public void  storeMultVec(Assembly assembly, int storeCount){
        if(storeCount>0){
            assembly.textArray.add("        STO     " + tempAssembly[2] + "\n");
        }else{
            assembly.textArray.add("        STO     "+ tempAssembly[1]+"\n");
        }
    }
    public  void storeMultVecCout(Assembly assembly, int storeCount){
        if(storeCount>0){
            assembly.textArray.add("        STO     " + tempAssembly[1] + "\n");
        }else{
            assembly.textArray.add("        STO     "+ tempAssembly[0]+"\n");
        }
    }

    public void vecReceivesVec(Assembly assembly){
        assembly.textArray.add("        STO     "+ tempAssembly[1]+"\n");
        assembly.textArray.add("        LD      "+ tempAssembly[0]+"\n");
        assembly.textArray.add("        STO     "+ "$indr"+"\n");
        assembly.textArray.add("        LD      "+ tempAssembly[1]+"\n");
    }
    public void loadLeftSide(Assembly assembly,String index){
        Boolean flag = Character.isDigit(index.charAt(0));
        if(flag) {
            assembly.textArray.add("        LDI      " + index + "\n");
        }else{
            assembly.textArray.add("        LD       " + index + "\n");
        }
        assembly.textArray.add("        STO     "+ tempAssembly[0]+"\n");
    }

    public void printAssemblyDataTable(Assembly assembly){
        System.out.print(assembly.dataArray.get(0));
        for(int i = 1;i<assembly.dataArray.size();i++){
            String init = "0";
            String name = assembly.dataArray.get(i).split(",")[0];
            int size = Integer.parseInt(assembly.dataArray.get(i).split(",")[2]);
            for(int j=1;j<size;j++){
                init = init + " , 0";
            }
            System.out.print("      "+name+" : " +init+"\n");
        }
    }

    public void printAssemblyTextTable(Assembly assembly){
        for(String x : assembly.textArray){
            System.out.print(x);
        }
    }

    public String getAssemblyString(Assembly assembly){
        String assemblyString = new String();
        assemblyString += assembly.dataArray.get(0).toString();
        for(int i = 1;i<assembly.dataArray.size();i++){
            String init = "0";
            String name = assembly.dataArray.get(i).split(",")[0];
            int size = Integer.parseInt(assembly.dataArray.get(i).split(",")[2]);
            for(int j=1;j<size;j++){
                init = init + " , 0";
            }
            assemblyString += "      "+name+" : " +init+"\n";
        }
        for(String x : assembly.textArray){
            assemblyString += x.toString();
        }
        return assemblyString;
    }



}