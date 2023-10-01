//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 2 "grammer.y"
package Sintactico;
import Lexico.AnalizadorLexico;
import java.util.Scanner;
import Tools.Logger;
//#line 22 "Parser.java"




public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class ParserVal is defined in ParserVal.java


String   yytext;//user variable to return contextual strings
ParserVal yyval; //used to return semantic vals from action routines
ParserVal yylval;//the 'lval' (result) I got from yylex()
ParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new ParserVal[YYSTACKSIZE];
  yyval=new ParserVal();
  yylval=new ParserVal();
  valptr=-1;
}
void val_push(ParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
ParserVal val_pop()
{
  if (valptr<0)
    return new ParserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
ParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new ParserVal();
  return valstk[ptr];
}
final ParserVal dup_yyval(ParserVal val)
{
  ParserVal dup = new ParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short CLASS=257;
public final static short INTERFACE=258;
public final static short IMPLEMENT=259;
public final static short NEW=260;
public final static short SUPER=261;
public final static short THIS=262;
public final static short FUNC=263;
public final static short RETURN=264;
public final static short IF=265;
public final static short ELSE=266;
public final static short END_IF=267;
public final static short FOR=268;
public final static short IN=269;
public final static short RANGE=270;
public final static short IMPL=271;
public final static short PRINT=272;
public final static short TOD=273;
public final static short EQUAL_OPERATOR=274;
public final static short NOT_EQUAL_OPERATOR=275;
public final static short GREATER_THAN_OR_EQUAL_OPERATOR=276;
public final static short LESS_THAN_OR_EQUAL_OPERATOR=277;
public final static short MINUS_ASSIGN=278;
public final static short VOID=279;
public final static short LONG=280;
public final static short UINT=281;
public final static short DOUBLE=282;
public final static short BOOLEAN=283;
public final static short CADENA=284;
public final static short ID=285;
public final static short CTE=286;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    1,    1,    2,    2,    2,    2,    3,    3,
    7,    9,    9,   10,   10,   11,   11,   13,   16,   16,
   17,   17,   18,   19,   14,   21,   23,   23,   24,   24,
   22,   22,   25,   27,   12,   29,    8,   30,   30,    5,
   32,   32,   33,   33,   34,    4,    4,   35,   37,   37,
   36,    6,    6,   40,   41,   41,   42,   43,   44,   20,
   45,   46,   46,   28,   50,   50,   50,   51,   51,   51,
   51,   51,   52,   52,   52,   53,   53,   53,   53,   53,
   54,   54,   55,   55,   56,   48,   49,   49,   47,   47,
   57,   57,   57,   59,   58,   58,   58,   61,   61,   61,
   61,   61,   61,   62,   62,   63,   63,   15,   15,   64,
   64,   39,   39,   60,   31,   65,   65,   67,   67,   68,
   66,   26,   26,   38,   38,   38,   69,   69,   71,   71,
   72,   74,   73,   73,   73,   73,   73,   73,   75,   75,
   75,   75,   81,   82,   82,   82,   80,   76,   77,   78,
   83,   84,   86,   85,   87,   87,   79,   79,   70,
};
final static short yylen[] = {                            2,
    1,    1,    1,    2,    1,    1,    1,    1,    3,    4,
    3,    1,    2,    1,    1,    1,    1,    3,    1,    3,
    1,    3,    1,    1,    2,    2,    1,    1,    4,    3,
    1,    1,    2,    1,    2,    1,    2,    1,    3,    3,
    3,    3,    1,    2,    2,    3,    2,    2,    4,    3,
    1,    5,    4,    3,    1,    2,    1,    2,    1,    1,
    3,    1,    1,    1,    1,    3,    3,    1,    3,    3,
    3,    3,    1,    3,    3,    1,    4,    3,    3,    3,
    1,    1,    1,    3,    1,    1,    3,    3,    1,    1,
    1,    1,    1,    1,    5,    4,    2,    5,    4,    7,
    6,    7,    6,    1,    0,    5,    4,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    3,    2,    4,    1,    3,    1,    2,    1,    1,
    2,    2,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    7,    9,   11,
    1,    1,    1,    1,    1,    3,    3,    1,    2,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    5,    6,    7,
    8,    0,    0,   28,  119,  118,  120,  111,  121,   27,
    0,  113,    0,  109,  112,  108,  110,  114,  116,  117,
    0,    4,    0,    0,    9,    0,    0,   40,    0,   48,
    0,    0,   46,   51,  125,    0,    0,   38,  115,   36,
    0,   12,   14,   15,   16,   17,    0,    0,    0,    0,
   10,    0,    0,    0,   43,    0,  159,    0,    0,   91,
    0,    0,    0,    0,    0,  147,  137,    0,  139,  144,
    0,   62,    0,    0,   92,  145,  146,    0,  142,  127,
  129,  130,    0,  133,  134,  135,  136,  138,  140,  141,
  143,    0,    0,   11,   13,   23,    0,   19,    0,   32,
   25,   31,    0,   26,    0,   35,   42,   45,   41,   44,
   50,    0,    0,  114,    0,    0,  151,    0,    0,    0,
  123,    0,    0,   90,   89,    0,    0,  126,    0,  128,
  104,  131,    0,   52,   39,   18,    0,    0,    0,   33,
   49,    0,    0,    0,   86,    0,   60,    0,    0,  157,
    0,   85,    0,    0,    0,   34,   82,    0,    0,    0,
    0,   76,   81,   83,  122,   61,    0,  124,    0,    0,
   55,   57,   20,   22,   24,   30,    0,   96,    0,    0,
    0,    0,    0,    0,    0,    0,   99,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   59,   58,   54,   56,   29,   95,    0,    0,   88,    0,
   87,    0,    0,   84,   98,    0,    0,    0,    0,    0,
    0,    0,    0,   78,   79,   80,    0,    0,  103,    0,
    0,    0,  152,    0,   77,  101,    0,  102,    0,  148,
  154,    0,  100,    0,  153,    0,  149,    0,    0,  150,
};
final static short yydgoto[] = {                          5,
    6,    7,    8,    9,   10,   11,   35,   36,   51,   52,
   53,   54,   55,   77,   20,  107,  108,  109,  184,  156,
   58,  111,   59,  114,  116,   79,  165,  166,   60,   47,
   22,   38,   64,   65,   23,   43,   40,   44,   24,  144,
  180,  181,  182,  212,   80,   81,  136,  167,   83,  168,
  169,  170,  171,  172,  173,  174,   84,   85,    0,   25,
   86,  142,   87,   26,   27,   28,   29,   30,   88,   89,
   90,   91,   92,   93,   94,   95,   96,   97,   98,   99,
  100,  101,  128,  244,  252,  256,    0,
};
final static short yysindex[] = {                      -141,
 -242, -240, -233, -214,    0, -141,    0,    0,    0,    0,
    0, -109,  -55,    0,    0,    0,    0,    0,    0,    0,
 -212,    0, -103,    0,    0,    0,    0,    0,    0,    0,
 -189,    0, -189,  184,    0,  -26, -110,    0,   61,    0,
   60,  801,    0,    0,    0,   50,   52,    0,    0,    0,
  -74,    0,    0,    0,    0,    0, -167,  -17, -162,  156,
    0,   81, -162,  -66,    0,  107,    0, -189,   89,    0,
   98, -144, -137,  109,  631,    0,    0, -167,    0,    0,
  -49,    0,    0,  105,    0,    0,    0,  704,    0,    0,
    0,    0,  108,    0,    0,    0,    0,    0,    0,    0,
    0,   30, -189,    0,    0,    0,   12,    0,   99,    0,
    0,    0,  116,    0, -167,    0,    0,    0,    0,    0,
    0,  118,  122,    0, -122, -220,    0, -104,  108,  -34,
    0,  743,  115,    0,    0,  -40, -118,    0,   57,    0,
    0,    0, -233,    0,    0,    0, -167, -220,  148,    0,
    0,  -32,  144,  139,    0,  146,    0,  145,  -80,    0,
  153,    0,  -40,  108,  147,    0,    0, -236,   10,   76,
   48,    0,    0,    0,    0,    0,  155,    0,  -17,  163,
    0,    0,    0,    0,    0,    0,  158,    0,  159,  -30,
  -89,  801,  -88,  170,  -38,  162,    0,  108,  -40,  -40,
  -40,  -40,  -40,  -40,  -40,  -40,  -38,  -38,  -38,  -16,
    0,    0,    0,    0,    0,    0,  108,  171,    0, -186,
    0,  -65,  181,    0,    0,   10,   10,   76,   76,   76,
   76,   48,   48,    0,    0,    0,  108,  182,    0,  108,
  801,  108,    0,  -61,    0,    0,  108,    0,  -41,    0,
    0,  -59,    0,  108,    0,  187,    0,  801,  108,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,  231,    1,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    3,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  -55,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  597,  -48,    0,    0,    0,    0,    0,    0,
    0,    0,  -42,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  854,    0,    0,    0,    0,    0,    0,    0,
    0,    5,    0,    0,    0,    0,    0,    0,  677,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  186,    0,    0,    0,    0,    0,  571,    0,
    0,    0,  775,    0,    0,    0,    0,    0,  827,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  -29,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  571,    0,    0,    0,  514,  418,   95,
   17,    0,    0,    0,    0,    0,  -28,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  571,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  571,    0,    0,    0,
    0,    0,    0,    0,    0,  444,  485,  142,  324,  353,
  391,   43,   69,    0,    0,    0,  571,    0,    0,  571,
    0,  571,    0,    0,    0,    0,  571,    0,    0,    0,
    0,    0,    0,  571,    0,    0,    0,    0,  571,    0,
};
final static short yygindex[] = {                         0,
    0,  228,    0,    0,    0,    0,  199,    0,    0,  189,
    0,    0,    0,    2,   49,  160,   97,  127,    0,  123,
 -113,   96,   18,  217,  -50,  -36, -121,  -99,    0,    0,
  -10,    0,    0,  237,    0,    0,    0,    0,  264,    0,
    0,  126,    0,    0,  -91,    0,    0,   32,    0,    0,
  -73,  -25,  -72,  -63,    0,    0,  -82,    0,    0,  244,
    0,  291,    0,    0,    0,   -5,    0,    0,  238,    6,
  -54,    0, -166,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,
};
final static int YYTABLESIZE=1139;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                        163,
    2,  163,   47,   93,   53,  163,  164,  163,  188,  163,
  217,  135,   86,   34,   62,  122,   88,   87,   63,   42,
   21,  112,   48,  163,  237,  220,  110,   49,   45,  179,
  189,   88,   87,  140,  157,   56,  176,  199,  200,   68,
  154,   70,   12,  158,   13,   14,   15,   16,   17,   18,
  104,   19,   56,   31,   63,  146,  157,   73,  119,   73,
   73,   73,  124,  196,  155,  158,  179,   37,  218,  203,
  147,  204,   39,   82,  249,   73,   73,  140,   73,  241,
  242,   63,   57,   74,  209,   74,   74,   74,  238,  207,
   78,  259,  145,  139,  208,   19,   34,   49,  187,   57,
   66,   74,   74,   67,   74,   75,   82,  102,  115,   75,
  103,   75,   75,   75,  115,    1,    2,  106,  205,   82,
  206,    3,  113,   78,  117,  226,  227,   75,   75,    4,
   75,  223,  232,  233,  125,   68,   78,  126,   68,   73,
  127,   73,  112,  234,  235,  236,  129,  121,  130,   33,
  137,  141,  143,   68,   68,  149,   68,   82,  151,  148,
   41,  152,  153,   82,  159,   74,  177,   74,   14,   15,
   16,   17,   18,  147,   19,  228,  229,  230,  231,   82,
   78,  178,   71,  190,  191,   71,  192,  198,  186,  194,
  193,   75,  195,   75,  210,  219,  221,  115,  215,  216,
   71,   71,  224,   71,   14,   15,   16,   17,   18,  222,
   50,  240,   14,   15,   16,   17,   18,   68,   19,   68,
  243,  245,  247,   82,  251,  254,  255,  258,  134,   86,
    1,   97,  161,   32,   61,   63,  121,  133,  161,  105,
  161,  150,  161,  183,  155,  162,  155,  162,   88,   87,
  155,  162,  155,  162,  155,  162,  161,    3,    3,   47,
   47,   53,   53,    3,   71,   47,   71,   53,  155,  162,
  185,    3,   82,   47,  211,   53,   73,   73,   73,  118,
   73,   73,   73,   73,   73,  201,  202,  213,   73,   82,
   73,   73,   73,   73,   46,   73,   73,   73,   73,   73,
  120,   73,   74,   74,   74,  214,   74,   74,   74,   74,
   74,  123,  132,    0,   74,    0,   74,   74,   74,   74,
    0,   74,   74,   74,   74,   74,    0,   74,   75,   75,
   75,    0,   75,   75,   75,   75,   75,    0,    0,    0,
   75,    0,   75,   75,   75,   75,    0,   75,   75,   75,
   75,   75,    0,   75,   68,   68,   68,    0,   68,   68,
   68,   68,   68,    0,   72,    0,   68,   72,   68,   68,
   68,   68,    0,   68,   68,   68,   68,   68,    0,   68,
    0,    0,   72,   72,    0,   72,   15,   16,   17,   18,
    0,   19,    0,   69,    0,    0,   69,    0,    0,    0,
    0,   71,   71,   71,    0,   71,   71,   71,   71,   71,
    0,   69,   69,   71,   69,   71,   71,   71,   71,  160,
   71,   71,   71,   71,   71,    0,   71,   15,   16,   17,
   18,   70,   19,    0,   70,   15,   16,   17,   18,    0,
   19,   14,   15,   16,   17,   18,   72,   19,   72,   70,
   70,    0,   70,    0,  197,    0,    0,    0,   65,    0,
    0,   65,   14,   15,   16,   17,   18,    0,   50,    0,
    0,    0,    0,    0,    0,   69,   65,   69,    0,    0,
    0,    0,    0,    0,   66,    0,    0,   66,  225,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   66,    0,    0,    0,    0,  239,    0,    0,
    0,    0,    0,   70,    0,   70,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   67,    0,  246,   67,    0,
  248,    0,  250,    0,    0,    0,    0,  253,    0,    0,
   65,    0,   65,   67,  257,    0,    0,    0,    0,  260,
    0,    0,    0,    0,   64,    0,    0,   64,    0,    0,
    0,    0,    0,    0,    0,    0,   66,    0,   66,    0,
    0,    0,   64,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   72,   72,   72,    0,   72,   72,   72,
   72,   72,    0,    0,    0,   72,    0,   72,   72,   72,
   72,    0,   72,   72,   72,   72,   72,   67,   72,   67,
    0,    0,   69,   69,   69,    0,   69,   69,   69,   69,
   69,    0,    0,    0,   69,    0,   69,   69,   69,   69,
    0,   69,   69,   69,   69,   69,   64,   69,   64,    0,
  158,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   70,   70,   70,    0,   70,   70,   70,   70,   70,    0,
    0,    0,   70,    0,   70,   70,   70,   70,    0,   70,
   70,   70,   70,   70,   76,   70,    0,   65,   65,   65,
    0,   65,   65,   65,   65,   65,    0,    0,    0,   65,
    0,   65,   65,  105,    0,  105,   65,   65,   65,   65,
   65,    0,   65,   66,   66,   66,    0,   66,   66,   66,
   66,   66,    0,    0,    0,   66,    0,   66,   66,  158,
   21,  158,   66,   66,   66,   66,   66,    0,   66,    0,
    0,    0,    0,    0,    0,   21,    0,    0,    0,    0,
    0,    0,    0,    0,   67,   67,   67,   76,   67,   67,
   67,   67,   67,   75,    0,  131,   67,    0,   67,   67,
    0,    0,    0,   67,   67,   67,   67,   67,    0,   67,
    0,    0,    0,   64,   64,   64,    0,   64,   64,   64,
   64,   64,    0,    0,    0,   64,   76,    0,    0,    0,
    0,    0,   64,   64,   64,   64,   64,    0,   64,   21,
    0,   21,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  132,    0,
    0,    0,    0,    0,    0,    0,   75,    0,  138,    0,
  105,  105,  105,    0,  105,  105,  105,  105,  105,    0,
    0,    0,  105,    0,   76,    0,    0,    0,    0,  105,
  105,  105,  105,  105,    0,  105,  158,  158,  158,    0,
  158,  158,  158,  158,  158,   75,    0,  175,  158,    0,
  142,    0,    0,    0,    0,  158,  158,  158,  158,  158,
    0,  158,    0,    0,    0,    0,    0,    0,    0,    0,
   68,   69,   70,    0,   41,   71,    0,  132,   72,  132,
    0,    0,   73,    0,    0,    0,    0,    0,    0,   14,
   15,   16,   17,   18,    0,   74,    0,    0,    0,    0,
    0,    0,    0,   75,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   21,   21,   21,    0,
   21,   21,    0,    0,   21,    0,    0,    0,   21,  142,
    0,    0,    0,    0,    0,   21,   21,   21,   21,   21,
    0,   21,    0,   68,   69,   70,    0,   41,   71,    0,
    0,   72,    0,    0,    0,   73,  105,    0,  105,    0,
    0,    0,   14,   15,   16,   17,   18,    0,   74,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   68,   69,   70,    0,   41,   71,    0,    0,
   72,    0,    0,    0,   73,    0,    0,    0,    0,    0,
    0,   14,   15,   16,   17,   18,    0,   74,    0,    0,
    0,    0,    0,    0,  132,  132,  132,    0,  132,  132,
    0,    0,  132,    0,    0,    0,  132,    0,    0,    0,
    0,    0,    0,  132,  132,  132,  132,  132,    0,  132,
   68,   69,   70,    0,   41,   71,    0,    0,   72,    0,
    0,    0,   73,    0,    0,    0,    0,    0,    0,   14,
   15,   16,   17,   18,    0,   74,  142,  142,  142,    0,
  142,  142,    0,    0,  142,    0,    0,    0,  142,    0,
    0,    0,    0,    0,    0,  142,  142,  142,  142,  142,
    0,  142,    0,  105,  105,  105,    0,  105,  105,    0,
    0,  105,    0,    0,    0,  105,    0,    0,    0,    0,
    0,    0,  105,  105,  105,  105,  105,    0,  105,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         40,
    0,   40,    0,   46,    0,   40,   41,   40,   41,   40,
   41,   61,   61,  123,  125,   66,   46,   46,   61,  123,
    3,   58,   33,   40,   41,  192,   44,   33,   23,  143,
  152,   61,   61,   88,  126,   34,  136,  274,  275,  260,
  261,  262,  285,  126,  285,  279,  280,  281,  282,  283,
  125,  285,   51,  268,   37,   44,  148,   41,  125,   43,
   44,   45,   68,  163,  285,  148,  180,  123,  190,   60,
   59,   62,  285,   42,  241,   59,   60,  132,   62,  266,
  267,   64,   34,   41,   37,   43,   44,   45,  210,   42,
   42,  258,  103,   88,   47,  285,  123,  103,  149,   51,
   40,   59,   60,   44,   62,  123,   75,   58,   60,   41,
   59,   43,   44,   45,   66,  257,  258,  285,   43,   88,
   45,  263,  285,   75,   44,  199,  200,   59,   60,  271,
   62,  195,  205,  206,   46,   41,   88,   40,   44,  123,
  285,  125,  179,  207,  208,  209,  284,   41,   40,  259,
   46,   44,  123,   59,   60,   40,   62,  126,   41,   61,
  264,   40,  285,  132,  269,  123,  285,  125,  279,  280,
  281,  282,  283,   59,  285,  201,  202,  203,  204,  148,
  132,  125,   41,   40,   46,   44,   41,   41,   41,  270,
   46,  123,   40,  125,   40,  285,  285,  149,   41,   41,
   59,   60,   41,   62,  279,  280,  281,  282,  283,   40,
  285,   41,  279,  280,  281,  282,  283,  123,  285,  125,
  286,   41,   41,  192,  286,  267,  286,   41,  278,  278,
    0,   46,  273,    6,   36,  278,  285,   78,  273,   51,
  273,  115,  273,  147,  285,  286,  285,  286,  278,  278,
  285,  286,  285,  286,  285,  286,  273,  257,  258,  257,
  258,  257,  258,  263,  123,  263,  125,  263,  285,  286,
  148,  271,  241,  271,  179,  271,  260,  261,  262,   63,
  264,  265,  266,  267,  268,  276,  277,  125,  272,  258,
  274,  275,  276,  277,   31,  279,  280,  281,  282,  283,
   64,  285,  260,  261,  262,  180,  264,  265,  266,  267,
  268,   68,   75,   -1,  272,   -1,  274,  275,  276,  277,
   -1,  279,  280,  281,  282,  283,   -1,  285,  260,  261,
  262,   -1,  264,  265,  266,  267,  268,   -1,   -1,   -1,
  272,   -1,  274,  275,  276,  277,   -1,  279,  280,  281,
  282,  283,   -1,  285,  260,  261,  262,   -1,  264,  265,
  266,  267,  268,   -1,   41,   -1,  272,   44,  274,  275,
  276,  277,   -1,  279,  280,  281,  282,  283,   -1,  285,
   -1,   -1,   59,   60,   -1,   62,  280,  281,  282,  283,
   -1,  285,   -1,   41,   -1,   -1,   44,   -1,   -1,   -1,
   -1,  260,  261,  262,   -1,  264,  265,  266,  267,  268,
   -1,   59,   60,  272,   62,  274,  275,  276,  277,  129,
  279,  280,  281,  282,  283,   -1,  285,  280,  281,  282,
  283,   41,  285,   -1,   44,  280,  281,  282,  283,   -1,
  285,  279,  280,  281,  282,  283,  123,  285,  125,   59,
   60,   -1,   62,   -1,  164,   -1,   -1,   -1,   41,   -1,
   -1,   44,  279,  280,  281,  282,  283,   -1,  285,   -1,
   -1,   -1,   -1,   -1,   -1,  123,   59,  125,   -1,   -1,
   -1,   -1,   -1,   -1,   41,   -1,   -1,   44,  198,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   59,   -1,   -1,   -1,   -1,  217,   -1,   -1,
   -1,   -1,   -1,  123,   -1,  125,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   41,   -1,  237,   44,   -1,
  240,   -1,  242,   -1,   -1,   -1,   -1,  247,   -1,   -1,
  123,   -1,  125,   59,  254,   -1,   -1,   -1,   -1,  259,
   -1,   -1,   -1,   -1,   41,   -1,   -1,   44,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  123,   -1,  125,   -1,
   -1,   -1,   59,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  260,  261,  262,   -1,  264,  265,  266,
  267,  268,   -1,   -1,   -1,  272,   -1,  274,  275,  276,
  277,   -1,  279,  280,  281,  282,  283,  123,  285,  125,
   -1,   -1,  260,  261,  262,   -1,  264,  265,  266,  267,
  268,   -1,   -1,   -1,  272,   -1,  274,  275,  276,  277,
   -1,  279,  280,  281,  282,  283,  123,  285,  125,   -1,
   44,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  260,  261,  262,   -1,  264,  265,  266,  267,  268,   -1,
   -1,   -1,  272,   -1,  274,  275,  276,  277,   -1,  279,
  280,  281,  282,  283,   44,  285,   -1,  260,  261,  262,
   -1,  264,  265,  266,  267,  268,   -1,   -1,   -1,  272,
   -1,  274,  275,  123,   -1,  125,  279,  280,  281,  282,
  283,   -1,  285,  260,  261,  262,   -1,  264,  265,  266,
  267,  268,   -1,   -1,   -1,  272,   -1,  274,  275,  123,
   44,  125,  279,  280,  281,  282,  283,   -1,  285,   -1,
   -1,   -1,   -1,   -1,   -1,   59,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  260,  261,  262,   44,  264,  265,
  266,  267,  268,  123,   -1,  125,  272,   -1,  274,  275,
   -1,   -1,   -1,  279,  280,  281,  282,  283,   -1,  285,
   -1,   -1,   -1,  260,  261,  262,   -1,  264,  265,  266,
  267,  268,   -1,   -1,   -1,  272,   44,   -1,   -1,   -1,
   -1,   -1,  279,  280,  281,  282,  283,   -1,  285,  123,
   -1,  125,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   44,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  123,   -1,  125,   -1,
  260,  261,  262,   -1,  264,  265,  266,  267,  268,   -1,
   -1,   -1,  272,   -1,   44,   -1,   -1,   -1,   -1,  279,
  280,  281,  282,  283,   -1,  285,  260,  261,  262,   -1,
  264,  265,  266,  267,  268,  123,   -1,  125,  272,   -1,
   44,   -1,   -1,   -1,   -1,  279,  280,  281,  282,  283,
   -1,  285,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  260,  261,  262,   -1,  264,  265,   -1,  123,  268,  125,
   -1,   -1,  272,   -1,   -1,   -1,   -1,   -1,   -1,  279,
  280,  281,  282,  283,   -1,  285,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  123,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  260,  261,  262,   -1,
  264,  265,   -1,   -1,  268,   -1,   -1,   -1,  272,  123,
   -1,   -1,   -1,   -1,   -1,  279,  280,  281,  282,  283,
   -1,  285,   -1,  260,  261,  262,   -1,  264,  265,   -1,
   -1,  268,   -1,   -1,   -1,  272,  123,   -1,  125,   -1,
   -1,   -1,  279,  280,  281,  282,  283,   -1,  285,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  260,  261,  262,   -1,  264,  265,   -1,   -1,
  268,   -1,   -1,   -1,  272,   -1,   -1,   -1,   -1,   -1,
   -1,  279,  280,  281,  282,  283,   -1,  285,   -1,   -1,
   -1,   -1,   -1,   -1,  260,  261,  262,   -1,  264,  265,
   -1,   -1,  268,   -1,   -1,   -1,  272,   -1,   -1,   -1,
   -1,   -1,   -1,  279,  280,  281,  282,  283,   -1,  285,
  260,  261,  262,   -1,  264,  265,   -1,   -1,  268,   -1,
   -1,   -1,  272,   -1,   -1,   -1,   -1,   -1,   -1,  279,
  280,  281,  282,  283,   -1,  285,  260,  261,  262,   -1,
  264,  265,   -1,   -1,  268,   -1,   -1,   -1,  272,   -1,
   -1,   -1,   -1,   -1,   -1,  279,  280,  281,  282,  283,
   -1,  285,   -1,  260,  261,  262,   -1,  264,  265,   -1,
   -1,  268,   -1,   -1,   -1,  272,   -1,   -1,   -1,   -1,
   -1,   -1,  279,  280,  281,  282,  283,   -1,  285,
};
}
final static short YYFINAL=5;
final static short YYMAXTOKEN=286;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,"'%'",null,null,"'('","')'","'*'","'+'",
"','","'-'","'.'","'/'",null,null,null,null,null,null,null,null,null,null,"':'",
"';'","'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,"'{'",null,"'}'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,"CLASS","INTERFACE","IMPLEMENT","NEW",
"SUPER","THIS","FUNC","RETURN","IF","ELSE","END_IF","FOR","IN","RANGE","IMPL",
"PRINT","TOD","EQUAL_OPERATOR","NOT_EQUAL_OPERATOR",
"GREATER_THAN_OR_EQUAL_OPERATOR","LESS_THAN_OR_EQUAL_OPERATOR","MINUS_ASSIGN",
"VOID","LONG","UINT","DOUBLE","BOOLEAN","CADENA","ID","CTE",
};
final static String yyrule[] = {
"$accept : program",
"program : type_declarations",
"program : type_declaration",
"type_declarations : type_declaration",
"type_declarations : type_declarations type_declaration",
"type_declaration : class_declaration",
"type_declaration : function_declaration",
"type_declaration : interface_declaration",
"type_declaration : implement_for_declaration",
"class_declaration : CLASS ID class_body",
"class_declaration : CLASS ID interfaces class_body",
"class_body : '{' class_body_declarations '}'",
"class_body_declarations : class_body_declaration",
"class_body_declarations : class_body_declarations class_body_declaration",
"class_body_declaration : class_member_declaration",
"class_body_declaration : constructor_declaration",
"class_member_declaration : field_declaration",
"class_member_declaration : method_declaration",
"field_declaration : type variable_declarators ','",
"variable_declarators : variable_declarator",
"variable_declarators : variable_declarators ';' variable_declarator",
"variable_declarator : variable_declarator_id",
"variable_declarator : variable_declarator_id '=' variable_initializer",
"variable_declarator_id : ID",
"variable_initializer : expression",
"method_declaration : method_header method_body",
"method_header : result_type method_declarator",
"result_type : type",
"result_type : VOID",
"method_declarator : ID '(' formal_parameter ')'",
"method_declarator : ID '(' ')'",
"method_body : block",
"method_body : ','",
"formal_parameter : type variable_declarator_id",
"real_parameter : right_hand_side",
"constructor_declaration : simple_type_name formal_parameter",
"simple_type_name : ID",
"interfaces : IMPLEMENT interface_type_list",
"interface_type_list : interface_type",
"interface_type_list : interface_type_list ';' interface_type",
"interface_declaration : INTERFACE ID interface_body",
"interface_body : '{' interface_member_declaration '}'",
"interface_body : '{' '}' ','",
"interface_member_declaration : interface_method_declaration",
"interface_member_declaration : interface_member_declaration interface_method_declaration",
"interface_method_declaration : result_type method_declarator",
"function_declaration : FUNC function_header function_body",
"function_declaration : FUNC function_header",
"function_header : result_type function_declarator",
"function_declarator : ID '(' formal_parameter ')'",
"function_declarator : ID '(' ')'",
"function_body : function_block",
"implement_for_declaration : IMPL FOR reference_type ':' implement_for_body",
"implement_for_declaration : IMPL FOR reference_type ':'",
"implement_for_body : '{' implement_for_body_declarations '}'",
"implement_for_body_declarations : implement_for_body_declaration",
"implement_for_body_declarations : implement_for_body_declarations implement_for_body_declaration",
"implement_for_body_declaration : implement_for_method_declaration",
"implement_for_method_declaration : method_header implement_for_method_body",
"implement_for_method_body : method_body",
"expression : assignment",
"assignment : left_hand_side assignment_operator right_hand_side",
"left_hand_side : expression_name",
"left_hand_side : field_access",
"right_hand_side : equality_expression",
"equality_expression : relational_expression",
"equality_expression : equality_expression EQUAL_OPERATOR relational_expression",
"equality_expression : equality_expression NOT_EQUAL_OPERATOR relational_expression",
"relational_expression : additive_expression",
"relational_expression : relational_expression '<' additive_expression",
"relational_expression : relational_expression '>' additive_expression",
"relational_expression : relational_expression GREATER_THAN_OR_EQUAL_OPERATOR additive_expression",
"relational_expression : relational_expression LESS_THAN_OR_EQUAL_OPERATOR additive_expression",
"additive_expression : multiplicative_expression",
"additive_expression : additive_expression '+' multiplicative_expression",
"additive_expression : additive_expression '-' multiplicative_expression",
"multiplicative_expression : unary_expression",
"multiplicative_expression : TOD '(' unary_expression ')'",
"multiplicative_expression : multiplicative_expression '*' unary_expression",
"multiplicative_expression : multiplicative_expression '/' unary_expression",
"multiplicative_expression : multiplicative_expression '%' unary_expression",
"unary_expression : term",
"unary_expression : expression_name",
"term : factor",
"term : '(' right_hand_side ')'",
"factor : CTE",
"expression_name : ID",
"field_access : primary '.' ID",
"field_access : SUPER '.' ID",
"assignment_operator : '='",
"assignment_operator : MINUS_ASSIGN",
"primary : THIS",
"primary : class_instance_creation_expression",
"primary : field_access",
"literal : CTE",
"class_instance_creation_expression : NEW class_type '(' real_parameter ')'",
"class_instance_creation_expression : NEW class_type '(' ')'",
"class_instance_creation_expression : NEW class_type",
"method_invocation : ID '(' real_parameter ')' invocation_end",
"method_invocation : ID '(' ')' invocation_end",
"method_invocation : primary '.' ID '(' real_parameter ')' invocation_end",
"method_invocation : primary '.' ID '(' ')' invocation_end",
"method_invocation : SUPER '.' ID '(' real_parameter ')' invocation_end",
"method_invocation : SUPER '.' ID '(' ')' invocation_end",
"invocation_end : ','",
"invocation_end :",
"function_invocation : ID '(' real_parameter ')' invocation_end",
"function_invocation : ID '(' ')' invocation_end",
"type : primitive_type",
"type : reference_type",
"primitive_type : numeric_type",
"primitive_type : BOOLEAN",
"reference_type : class_type",
"reference_type : interface_type",
"class_type : type_name",
"interface_type : type_name",
"numeric_type : integral_type",
"numeric_type : floating_type",
"integral_type : UINT",
"integral_type : LONG",
"floating_type : DOUBLE",
"type_name : ID",
"block : '{' block_statements '}'",
"block : '{' '}'",
"function_block : '{' block_statements return_statement '}'",
"function_block : return_statement",
"function_block : '{' block_statements '}'",
"block_statements : block_statement",
"block_statements : block_statements block_statement",
"block_statement : local_variable_declaration_statement",
"block_statement : statement",
"local_variable_declaration_statement : local_variable_declaration invocation_end",
"local_variable_declaration : type variable_declarators",
"statement : statement_without_trailing_substatement",
"statement : if_then_statement",
"statement : if_then_else_statement",
"statement : for_in_range_statement",
"statement : method_declaration",
"statement : print_statement",
"statement_without_trailing_substatement : block",
"statement_without_trailing_substatement : empty_statement",
"statement_without_trailing_substatement : expression_statement",
"statement_without_trailing_substatement : return_statement",
"expression_statement : statement_expression",
"statement_expression : assignment",
"statement_expression : method_invocation",
"statement_expression : function_invocation",
"empty_statement : ','",
"if_then_statement : IF '(' expression ')' statement END_IF invocation_end",
"if_then_else_statement : IF '(' expression ')' statement ELSE statement END_IF invocation_end",
"for_in_range_statement : FOR for_variable IN RANGE '(' for_init for_end for_update ')' statement invocation_end",
"for_variable : ID",
"for_init : CTE",
"for_update : CTE",
"for_end : CTE",
"statement_expression_list : statement_expression",
"statement_expression_list : statement_expression_list ';' statement_expression",
"print_statement : PRINT CADENA invocation_end",
"print_statement : PRINT",
"return_statement : RETURN ','",
};

//#line 404 "grammer.y"

private static AnalizadorLexico aLexico;

// This method is the one where BYACC/J expects to obtain its input tokens. 
// Wrap any file/string scanning code you have in this function. This method should return <0 if there is an error, and 0 when it encounters the end of input. See the examples to clarify what we mean.
int yylex() {
    
    int token = -1;
    while (!aLexico.hasFinishedTokenizer()) {
        token = aLexico.generateToken();
        if(token >= 0 ){ // deberia devolver cuando llega a un estado final
            // yyval = new ParserVal(token);
            return token;
        }

    }
    
    return token;
}


// This method is expected by BYACC/J, and is used to provide error messages to be directed to the channels the user desires.
void yyerror(String msg) {
    System.out.println(msg);
}

public static void main (String [] args){
    System.out.println("Iniciando compilacion... ");
    System.out.print("Ingrese el nombre del archivo binario a compilar: ");

    Scanner scanner = new Scanner(System.in);
    String input = scanner.nextLine();
    scanner.close();

    aLexico = new AnalizadorLexico(input);

    if ( !aLexico.hasReadWell() ) {
        return;
    }

    Parser aSintactico = new Parser();
    aSintactico.run();


    Logger.logError(1, "Este es un error.");
    Logger.logWarning(2, "Esta es una advertencia.");

    Logger.dumpLog();
    System.out.println(aLexico.getProgram());
}


//#line 746 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 47:
//#line 141 "grammer.y"
{Logger.logError(0, "Es necesario implementar el cuerpo de la funcion.");}
break;
case 53:
//#line 155 "grammer.y"
{Logger.logError(0, "Es necesario implementar el cuerpo del metodo.");}
break;
case 97:
//#line 248 "grammer.y"
{Logger.logError(0, "Se esperaba un \'(\'.");}
break;
case 105:
//#line 262 "grammer.y"
{Logger.logError(0, "Se esperaba una \",\".");}
break;
case 126:
//#line 322 "grammer.y"
{Logger.logError(0, "Es necesario declarar el returno de la funcion.");}
break;
case 158:
//#line 395 "grammer.y"
{Logger.logError(0, "Se esperaba una cadena.");}
break;
//#line 919 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public Parser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public Parser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
