package compiladoresM2;

import gals.SemanticError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Branches {
    public ArrayList<BranchCounter> branchPile = new ArrayList<>();
    public int branchCounter=0;
    private ArrayList<String> leftSideLex = new ArrayList<>();
    private ArrayList<String> rightSideLex = new ArrayList<>();
    private String relationalOPs[] = {">",">=","<","<=","==","!="};

    private String getOP(){
        for(String elem : Main.sem.attrPileOfLex){
            for(String op : relationalOPs){
                if(elem.equals(op)){
                    return elem;
                }
            }
        }
        return null;
    }

    private int checkFatherSide(List<String> list){
        //checking element in the middle
        int count  =0;
        for(int i=0;i<list.size();i++){
            for(String op : relationalOPs){
                if(list.get(i).equals(op)){
                    return count;
                }
            }
            count ++;
        }
        return 0;
    }
    private ArrayList<String> getLex(boolean isLeft){
        ArrayList<String> listToReturn = new ArrayList<>();
        if(isLeft){
            for(String x : Main.sem.attrPileOfLex){
                for(String op: relationalOPs){
                    if(x.equals(op)){
                        return listToReturn;
                    }
                }
                listToReturn.add(x);
            }
        }
        else{

            for(int y=1+checkFatherSide(Main.sem.expPilesOfBranches);y<Main.sem.attrPileOfLex.size();y++){
                listToReturn.add(Main.sem.attrPileOfLex.get(y));
            }
        }
        return listToReturn;
    }
    public ArrayList<Integer> checkNumberOfExpInPile(ArrayList<String> pile, boolean isLeft){
        ArrayList<Integer> listOfExp = new ArrayList<>();

        int diff =0;
        if(!isLeft){
            diff = Main.sem.attrPileOfLex.size()-pile.size();
        }
        //fill list
        for(int i =0;i<pile.size();i++){
            listOfExp.add(i);
        }

        //check for elements inside indexList
        for(int x : listOfExp){
            for(IndexHelper index : Main.sem.indexList){
                if(index.positionInBranch-diff==x){
                    listOfExp.set(x,-1);
                }
            }
        }
        for(int x:listOfExp){
            if(x>=0){
                if(Main.sem.checkIfElementIsVect(Main.sem.symbolTable,pile.get(x))){
                    listOfExp.set(x,-1);
                }
            }
        }
        listOfExp.removeIf( name -> name.equals(-1));

        //Getting sums and red and deleting those who does not belong exp
        for(int i =0;i<listOfExp.size();i++){
            for(String ops : Main.sem.listOfOps){
                if(pile.get(listOfExp.get(i)).equals(ops)){
                    listOfExp.set(i,-1);
                    break;
                }
            }
        }
        listOfExp.removeIf( name -> name.equals(-1));


        return listOfExp;
    }

    private void treatingExp(ArrayList<String> pile,boolean isLeft){
        ArrayList<Integer> numberOfExp;
        numberOfExp= checkNumberOfExpInPile(pile,isLeft);

        //lembrar de tratar caso seja o primeiro da pilha e nao tenha + ou - na frente
        for(int i=0;i<numberOfExp.size();i++){
            String ops;
            if(numberOfExp.get(i)==0){
                ops="+";
            }else{
                ops=pile.get(numberOfExp.get(i)-1);
            }
            Main.sem.assembly.addToIndex(pile.get(numberOfExp.get(i)),ops,Main.sem.assembly);
        }

    }
    private void operateWithIndexesMultiVec(int initialPointOfIndexList, int finalPointOfIndexList, int storeCount, int numberOfVecs,boolean isLeft) throws  SemanticError{
        if (Main.sem.indexList.size() == 1) {
            int position = Main.sem.indexList.get(0).positionInBranch;
            if (Main.sem.semanticTable.atribType(0, Main.sem.helper.changeTypeStringToInt(Main.sem.expPilesOfBranches.get(position))) != 0) {
                throw new SemanticError("Tipo da variavel se difere do esperado");
            }
            Main.sem.assembly.oneIndexStart(Main.sem.assembly, Main.sem.attrPileOfLex.get(Main.sem.indexList.get(0).positionInBranch));

        } else {
            int position;
            int position2;
            int positionOps;
            int result;
            if (initialPointOfIndexList != finalPointOfIndexList) {
                position = initialPointOfIndexList;
                position2 = initialPointOfIndexList + 2;
                positionOps = initialPointOfIndexList + 1;
                result = Main.sem.semanticTable.resultType(Main.sem.helper.changeTypeStringToInt(Main.sem.expPilesOfBranches.get(position)), Main.sem.helper.changeTypeStringToInt(Main.sem.expPilesOfBranches.get(position2)), Main.sem.helper.changeOpsToInt(Main.sem.expPilesOfBranches.get(positionOps)));
                if (result != 0) {
                    throw new SemanticError("Tipo da variavel se difere do esperado");
                }
                Main.sem.assembly.oneIndexStart(Main.sem.assembly, Main.sem.attrPileOfLex.get(position));
                Main.sem.assembly.addToIndex(Main.sem.attrPileOfLex.get(position2), Main.sem.attrPileOfLex.get(positionOps), Main.sem.assembly);

            }
            //in case one element
            else {
                position = initialPointOfIndexList;
                result = Main.sem.semanticTable.atribType(0, Main.sem.helper.changeTypeStringToInt(Main.sem.expPilesOfBranches.get(position)));
                Main.sem.assembly.oneIndexStart(Main.sem.assembly, Main.sem.attrPileOfLex.get(position));
            }
            for (int i = initialPointOfIndexList + 3; i < finalPointOfIndexList; i++) {
                position = i+1;
                positionOps =  i;
                result = Main.sem.semanticTable.resultType(result, Main.sem.helper.changeTypeStringToInt(Main.sem.expPilesOfBranches.get(position)), Main.sem.helper.changeOpsToInt(Main.sem.expPilesOfBranches.get(positionOps)));
                if (result != 0) {
                    throw new SemanticError("Tipo da variavel se difere do esperado");
                }
                Main.sem.assembly.addToIndex(Main.sem.attrPileOfLex.get(position), Main.sem.attrPileOfLex.get(positionOps), Main.sem.assembly);
            }
        }
        Main.sem.assembly.endIndex(Main.sem.attrPileOfLex.get(initialPointOfIndexList - 1), Main.sem.assembly);
        if (storeCount > 0 && isLeft) {
            Main.sem.assembly.storeTemp1(Main.sem.assembly);
            String op;
            if (initialPointOfIndexList - 2 >= 0) {
                op = Main.sem.attrPileOfLex.get(initialPointOfIndexList - 2);
            } else {
                op = "+";
            }
            Main.sem.assembly.multiVecOnBranch(Main.sem.assembly, op,isLeft);
        } else if(storeCount ==0 && isLeft){
            Main.sem.assembly.storeTemp0(Main.sem.assembly);
        }
        else if(storeCount>0 && !isLeft){
            Main.sem.assembly.storeTemp2(Main.sem.assembly);
            String op;
            if (initialPointOfIndexList - 2 >= 0) {
                op = Main.sem.attrPileOfLex.get(initialPointOfIndexList - 2);
            } else {
                op = "+";
            }
            Main.sem.assembly.multiVecOnBranch(Main.sem.assembly, op,isLeft);
        }else{
            Main.sem.assembly.storeTemp1(Main.sem.assembly);
        }

    }
    private void operateWithIndexesSingleVec(int initialPointOfIndexList, int finalPointOfIndexList, int storeCount, int numberOfVecs,boolean isLeft) throws SemanticError {

            if (Main.sem.indexList.size() == 1) {
                int position = Main.sem.indexList.get(0).positionInBranch;
                if (Main.sem.semanticTable.atribType(0, Main.sem.helper.changeTypeStringToInt(Main.sem.expPilesOfBranches.get(position))) != 0) {
                    throw new SemanticError("Tipo da variavel se difere do esperado");
                }
                Main.sem.assembly.oneIndexStart(Main.sem.assembly, Main.sem.attrPileOfLex.get(Main.sem.indexList.get(0).positionInBranch));

            } else {
                int position;
                int position2;
                int positionOps;
                int result;
                if (initialPointOfIndexList != finalPointOfIndexList) {
                    position = initialPointOfIndexList;
                    position2 = initialPointOfIndexList + 2;
                    positionOps = initialPointOfIndexList + 1;
                    result = Main.sem.semanticTable.resultType(Main.sem.helper.changeTypeStringToInt(Main.sem.expPilesOfBranches.get(position)), Main.sem.helper.changeTypeStringToInt(Main.sem.expPilesOfBranches.get(position2)), Main.sem.helper.changeOpsToInt(Main.sem.expPilesOfBranches.get(positionOps)));
                    if (result != 0) {
                        throw new SemanticError("Tipo da variavel se difere do esperado");
                    }
                    Main.sem.assembly.oneIndexStart(Main.sem.assembly, Main.sem.attrPileOfLex.get(position));
                    Main.sem.assembly.addToIndex(Main.sem.attrPileOfLex.get(position2), Main.sem.attrPileOfLex.get(positionOps), Main.sem.assembly);

                }
                //in case one element
                else {
                    position = initialPointOfIndexList;
                    result = Main.sem.semanticTable.atribType(0, Main.sem.helper.changeTypeStringToInt(Main.sem.expPilesOfBranches.get(position)));
                    Main.sem.assembly.oneIndexStart(Main.sem.assembly, Main.sem.attrPileOfLex.get(position));
                }
                for (int i = initialPointOfIndexList + 3; i < finalPointOfIndexList; i++) {
                    position = initialPointOfIndexList + i;
                    positionOps = initialPointOfIndexList + i + 1;
                    result = Main.sem.semanticTable.resultType(result, Main.sem.helper.changeTypeStringToInt(Main.sem.expPilesOfBranches.get(position)), Main.sem.helper.changeOpsToInt(Main.sem.expPilesOfBranches.get(positionOps)));
                    if (result != 0) {
                        throw new SemanticError("Tipo da variavel se difere do esperado");
                    }
                    Main.sem.assembly.addToIndex(Main.sem.attrPileOfLex.get(position), Main.sem.attrPileOfLex.get(positionOps), Main.sem.assembly);
                }
            }
            Main.sem.assembly.endIndex(Main.sem.attrPileOfLex.get(initialPointOfIndexList - 1), Main.sem.assembly);

    }
    private void treatingVects(boolean isLeft, int storeCount) throws SemanticError {
        ArrayList<String> sideLex = getLex(isLeft);
        if(isLeft){leftSideLex=sideLex;}
        else{rightSideLex=sideLex;}
        int numberOfVecs = Main.sem.checkNumberOfVecsInPile(sideLex);
        ArrayList<IndexHelper> listOfFirst = Main.sem.getFirstPositionOfIndex();
        ArrayList<IndexHelper> listOfLast = Main.sem.getLastPositionOfIndex();

        storeCount = 0;
        if(isLeft){
            listOfFirst.removeIf( name -> Integer.parseInt(name.fatherPosition)>checkFatherSide(Main.sem.attrPileOfLex));
            listOfLast.removeIf( name -> Integer.parseInt(name.fatherPosition)>checkFatherSide(Main.sem.attrPileOfLex));

        }
        else{
            listOfFirst.removeIf( name -> Integer.parseInt(name.fatherPosition)<checkFatherSide(Main.sem.attrPileOfLex));
            listOfLast.removeIf( name -> Integer.parseInt(name.fatherPosition)<checkFatherSide(Main.sem.attrPileOfLex));

        }

        if(numberOfVecs==1) {
            for (int i = 0; i < numberOfVecs; i++) {
                operateWithIndexesSingleVec(listOfFirst.get(i).positionInBranch, listOfLast.get(i).positionInBranch, storeCount, numberOfVecs,isLeft);
                storeCount++;
            }
        }else {
            for (int i = 0; i < numberOfVecs; i++) {
                operateWithIndexesMultiVec(listOfFirst.get(i).positionInBranch, listOfLast.get(i).positionInBranch, storeCount, numberOfVecs,isLeft);
                storeCount++;
            }
        }

    }

    public void justDOit(){
        BranchCounter obj = new BranchCounter();
        branchCounter++;
        obj.number = branchCounter;
        obj.lastBranchType = "do";
        branchPile.add(obj);
        Main.sem.assembly.label(Main.sem.assembly, branchPile.get(branchPile.size()-1).number);
    }

    private void validateExpression(ArrayList<Integer> list,boolean isLeft) throws SemanticError {
        String token1 = Main.sem.expPilesOfBranches.get(list.get(0));
        String tokenOP = Main.sem.expPilesOfBranches.get(list.get(1));
        String token2 = Main.sem.expPilesOfBranches.get(list.get(2));
        int result = Main.sem.semanticTable.resultType(Main.sem.helper.changeTypeStringToInt(token1), Main.sem.helper.changeTypeStringToInt(token2), Main.sem.helper.changeOpsToInt(tokenOP));
        if (result != 0) {
            throw new SemanticError("Tipo da variavel se difere do esperado");
        }
        Main.sem.assembly.verifyBranchExp(Main.sem.assembly,Main.sem.attrPileOfLex.get(list.get(0)));
        Main.sem.assembly.addToIndex(Main.sem.attrPileOfLex.get(list.get(2)),Main.sem.attrPileOfLex.get(list.get(1)),Main.sem.assembly);

        for(int i=3;i<list.size();i=i+2){
            token1 = Main.sem.expPilesOfBranches.get(list.get(i+1));
            tokenOP = Main.sem.expPilesOfBranches.get(list.get(i));
            result = Main.sem.semanticTable.resultType(result, Main.sem.helper.changeTypeStringToInt(token1), Main.sem.helper.changeOpsToInt(tokenOP));
            if (result != 0) {
                throw new SemanticError("Tipo da variavel se difere do esperado");
            }
            Main.sem.assembly.addToIndex(Main.sem.attrPileOfLex.get(list.get(i+1)),Main.sem.attrPileOfLex.get(list.get(i)),Main.sem.assembly);

        }


    }
    public void end(String typeOfCondition){
        if(typeOfCondition.equals("condition")){
            if(branchPile.get(branchPile.size()-1).lastBranchType.equals("while")){
                Main.sem.assembly.makeJump(Main.sem.assembly, branchPile.get(branchPile.size()-1).number);
                BranchCounter obj = new BranchCounter();
                branchCounter++;
                obj.number = branchCounter;
                obj.lastBranchType = "while";
                branchPile.add(obj);
                Main.sem.assembly.label(Main.sem.assembly,branchPile.get(branchPile.size()-1).number);
                branchPile.remove(branchPile.size()-1);
                branchPile.remove(branchPile.size()-1);
                for(int i=0;i<Main.sem.assembly.textArray.size();i++){
                    if(Main.sem.assembly.textArray.get(i).contains("-1")){
                        Main.sem.assembly.textArray.set(i,Main.sem.assembly.textArray.get(i).replace("-1",Integer.toString(branchCounter)));
                    }
                }

            }
            else {
                Main.sem.assembly.label(Main.sem.assembly, branchPile.get(branchPile.size() - 1).number);
                branchPile.remove(branchPile.size() - 1);
            }
        }
        else if(typeOfCondition.equals("starting_else")){
            Main.sem.assembly.makeJump(Main.sem.assembly, -1);
            Main.sem.assembly.label(Main.sem.assembly, branchPile.get(branchPile.size() - 1).number);
            branchPile.remove(branchPile.size() - 1);
            Main.sem.endElse = true;

        }
        else if(typeOfCondition.equals("end_else")){
            BranchCounter obj = new BranchCounter();
            branchCounter++;
            obj.number = branchCounter;
            obj.lastBranchType = "end_else";
            branchPile.add(obj);
            Main.sem.assembly.label(Main.sem.assembly, branchPile.get(branchPile.size() - 1).number);
            branchPile.remove(branchPile.size() - 1);
            Main.sem.endElse=false;

            for(int i=0;i<Main.sem.assembly.textArray.size();i++){
                if(Main.sem.assembly.textArray.get(i).contains("-1")){
                    Main.sem.assembly.textArray.set(i,Main.sem.assembly.textArray.get(i).replace("-1",Integer.toString(branchCounter)));
                }
            }
        }
    }
    private void whileValidation(){
        //isso teria que ser feito no final pois a pilha ja estaria somada para caso um if dentro do while por exemplo - pra saber onde pular caso nao entre mais no while
        Main.sem.assembly.branchNaming(Main.sem.assembly, -1,getOP());
    }

    public void ifCondition()throws  SemanticError{
        if(Main.sem.isWhile && !Main.sem.isDo){
            setupWhile();
            validateRelationalExp(Main.sem.expPilesOfBranches,0);
            //isso teria que ser feito no final pois a pilha ja estaria somada para caso um if dentro do while por exemplo - pra saber onde pular caso nao entre mais no while
            whileValidation();
        }
        else if(Main.sem.isWhile && Main.sem.isDo){
            validateRelationalExp(Main.sem.expPilesOfBranches,0);
            Main.sem.assembly.branchNaming(Main.sem.assembly,branchPile.get(branchPile.size()-1).number,getOP());
        }
        else{
            validateRelationalExp(Main.sem.expPilesOfBranches,0);
            setupIf();
        }
    }

    private void setupIf(){
        BranchCounter obj = new BranchCounter();
        branchCounter++;
        obj.number = branchCounter;
        obj.lastBranchType = "if";
        branchPile.add(obj);
        Main.sem.assembly.branchNaming(Main.sem.assembly,branchCounter,getOP());
    }
    private void setupWhile(){
        BranchCounter obj = new BranchCounter();
        branchCounter++;
        obj.number = branchCounter;
        obj.lastBranchType = "while";
        branchPile.add(obj);
        Main.sem.assembly.label(Main.sem.assembly,branchPile.get(branchPile.size()-1).number);
    }
    private void validateRelationalExp(ArrayList<String> list, int counter) throws SemanticError {
        ArrayList left = validateLeftSideOfOP(list);
        ArrayList right =validateRightSideOfOp(list,counter);
        if(!Main.sem.isFor) {
            compareSides(left, right);
        }else{
            compareSidesFOR(left,right);
        }
    }
    public ArrayList<Integer> validateLeftSideOfOP(ArrayList<String> list){
        ArrayList<Integer> left = new ArrayList<>();
        List<String> opsList = new ArrayList<>(Arrays.asList(relationalOPs));

        for(int i = 0;i<list.size();i++){
            if(!opsList.contains(list.get(i))){
                left.add(i);
            }
            else{
                return left;
            }
        }
        return left;
    }
    public ArrayList<Integer> validateRightSideOfOp(ArrayList<String> list, int leftCounter){
        ArrayList<Integer> right = new ArrayList<>();
        ArrayList<Integer> temp = new ArrayList<>();
        List<String> opsList = new ArrayList<>(Arrays.asList(relationalOPs));

        temp = validateLeftSideOfOP(list);
        for(int i = 0;i<list.size();i++) {
            if(!opsList.contains(list.get(i)) && !temp.contains(i)){
                right.add(i+leftCounter);
            }
        }
        return right;
    }
    private void loadOneSimpleVec(boolean isLeft){
        if(isLeft) {
            Main.sem.assembly.simpleVarReceivesIntOrVar(Main.sem.attrPileOfLex.get(1), Main.sem.assembly);
            Main.sem.assembly.endIndex(Main.sem.attrPileOfLex.get(0), Main.sem.assembly);
            Main.sem.assembly.storeTemp0(Main.sem.assembly);
        }else{
            ArrayList<String> sideLex = getLex(isLeft);
            rightSideLex=sideLex;
            Main.sem.assembly.simpleVarReceivesIntOrVar(rightSideLex.get(1), Main.sem.assembly);
            Main.sem.assembly.endIndex(rightSideLex.get(0), Main.sem.assembly);
            Main.sem.assembly.storeTemp1(Main.sem.assembly);
        }
    }
    public void leftExp(ArrayList<Integer> list) throws SemanticError {
        if(list.size()==1){
            Main.sem.assembly.verifyBranchExp(Main.sem.assembly,Main.sem.attrPileOfLex.get(list.get(0)));
            Main.sem.assembly.storeTemp0(Main.sem.assembly);
        }
        //1 arr
        else if(list.size()==2){
            loadOneSimpleVec(true);
        }
        else if(Main.sem.checkNumberOfVecsInPile(Main.sem.attrPileOfLex)>0 && list.size()>2){
            treatingVects(true,0);
            treatingExp(leftSideLex,true);
            Main.sem.assembly.storeTemp0(Main.sem.assembly);
        }
        else{
            validateExpression(list,true);
        }
    }
    public void rightExp(ArrayList<Integer> list) throws SemanticError {
        if(list.size()==1){
            Main.sem.assembly.verifyBranchExp(Main.sem.assembly,Main.sem.attrPileOfLex.get(list.get(0)));
            Main.sem.assembly.storeTemp1(Main.sem.assembly);
        }
        else  if(list.size()==2){
            loadOneSimpleVec(false);
        }
        else if(Main.sem.checkNumberOfVecsInPile(Main.sem.attrPileOfLex)>0 && list.size()>2){
            treatingVects(false,0);
            treatingExp(rightSideLex,false);
            Main.sem.assembly.storeTemp1(Main.sem.assembly);
        }
        else{
            validateExpression(list,false);
        }
    }
    private void compareTemp(){
        Main.sem.assembly.compareTemp(Main.sem.assembly);
    }
    public  void compareSides(ArrayList<Integer> left, ArrayList<Integer> right) throws SemanticError {
        leftExp(left);
        rightExp(right);
        compareTemp();
    }
    private void leftExpFOR(ArrayList<Integer> left){
        if(left.size()==1){

        }
        if(left.size()==2){

        }
        if(left.size()>2){

        }
    }
    private void rightExpFOR(ArrayList<Integer> right){
        if(right.size()==1){
            Main.sem.assembly.simpleVarReceivesIntOrVar(Main.sem.attrPileOfLex.get(right.get(0)),Main.sem.assembly);
            Main.sem.assembly.storeTemp0(Main.sem.assembly);
        }
        if(right.size()==2){

        }
        if(right.size()>2){

        }
    }

    public  void compareSidesFOR(ArrayList<Integer> left, ArrayList<Integer> right) throws SemanticError {
        leftExpFOR(left);
        rightExpFOR(right);

    }

    private void handleFirst(ArrayList<String> first) throws SemanticError {
        if(Main.sem.checkNumberOfVecsInPile(first)>0){

        }else{
            int firstEl = Main.sem.helper.changeTypeStringToInt(first.get(0));
            int secEl = Main.sem.helper.changeTypeStringToInt(first.get(1));
            int result = Main.sem.semanticTable.atribType(firstEl,secEl);
            if(result!=0){
                throw new SemanticError("Operacao de atribuicao invalida");

            }
            Main.sem.assembly.simpleVarReceivesIntOrVar(Main.sem.attrPileOfLex.get(1),Main.sem.assembly);
            Main.sem.assembly.store(Main.sem.attrPileOfLex.get(0),Main.sem.assembly, false);
        }
    }
    private void handleSelfIterate(){
        String selfIterateVar = Main.sem.attrPileOfLex.get(Main.sem.attrPileOfLex.size()-2);
        Main.sem.assembly.simpleVarReceivesIntOrVar(selfIterateVar,Main.sem.assembly);
        Main.sem.assembly.storeMultVecCout(Main.sem.assembly, 1);
    }
    private ArrayList<String> getFirstExpOfFor(ArrayList<String>first){
        for(String x: Main.sem.expPilesOfBranches){
            if(!x.equals(";")){
                first.add(x);
            }else {return first;}
        }
        return first;
    }
    private ArrayList<String> getExpFor(ArrayList<String>sec, int counter){
        int cont = 0;
        for(String x: Main.sem.expPilesOfBranches){
            if(x.equals(";")){
                cont ++;
            }
            if(!x.equals(";")&& cont==counter){
                sec.add(x);
            }
        }
        return sec;
    }

    private void handleLast(ArrayList<String> third){
        Main.sem.assembly.simpleVarReceivesIntOrVar(Main.sem.attrPileOfLex.get(Main.sem.attrPileOfLex.size()-2),Main.sem.assembly);
        Main.sem.assembly.storeTemp1(Main.sem.assembly);
    }
    private void handleForLabel(String op){
        branchCounter++;
        BranchCounter branchCounterObj = new BranchCounter();
        branchCounterObj.number=branchCounter;
        branchCounterObj.lastBranchType="for";
        branchPile.add(branchCounterObj);
        Main.sem.assembly.label(Main.sem.assembly,branchPile.get(branchPile.size()-1).number);
        Main.sem.assembly.subTempFor(Main.sem.assembly);
        Main.sem.assembly.branchNaming(Main.sem.assembly,-1,op);
    }
    public String forExp() throws SemanticError {
        ArrayList<String> first = new ArrayList<>();
        //first check
        first = getFirstExpOfFor(first);
        handleFirst(first);
        //sec check
        ArrayList<String> sec = new ArrayList<>();
        sec = getExpFor(sec,1);
        //+1 because of the ";"
        validateRelationalExp(sec,first.size()+1);

        ArrayList<String> third = new ArrayList<>();
        third = getExpFor(third,2);
        handleLast(third);

        Main.sem.assembly.simpleVarReceivesIntOrVar(Main.sem.attrPileOfLex.get(0),Main.sem.assembly);
        String ops = getOP();
        handleForLabel(ops);
        return Main.sem.attrPileOfLex.get(0);
    }
    public void endFor(String firstElement){
        Main.sem.assembly.simpleVarReceivesIntOrVar(firstElement,Main.sem.assembly);
        Main.sem.assembly.addTempFor(Main.sem.assembly);
        Main.sem.assembly.storeEL(Main.sem.assembly,firstElement);
        Main.sem.assembly.makeJump(Main.sem.assembly,branchPile.get(branchPile.size()-1).number);
        branchCounter++;
        BranchCounter branchCounterObj = new BranchCounter();
        branchCounterObj.number=branchCounter;
        branchCounterObj.lastBranchType="for_end";
        branchPile.add(branchCounterObj);
        Main.sem.assembly.label(Main.sem.assembly,branchPile.get(branchPile.size()-1).number);

        ///changing -1
        branchPile.remove(branchPile.size()-1);
        branchPile.remove(branchPile.size()-1);
        for(int i=0;i<Main.sem.assembly.textArray.size();i++){
            if(Main.sem.assembly.textArray.get(i).contains("-1")){
                Main.sem.assembly.textArray.set(i,Main.sem.assembly.textArray.get(i).replace("-1",Integer.toString(branchCounter)));
            }
        }
        Main.sem.isFor=false;

    }
}
