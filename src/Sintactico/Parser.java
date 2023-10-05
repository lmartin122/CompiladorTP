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

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import Lexico.PalabrasReservadasTabla;
import Tools.Logger;
//#line 22 "Parser.java"

public class Parser {

  boolean yydebug; // do I want debug output?
  int yynerrs; // number of errors so far
  int yyerrflag; // was there an error?
  int yychar; // the current working character

  // ########## MESSAGES ##########
  // ###############################################################
  // method: debug
  // ###############################################################
  void debug(String msg) {
    if (yydebug)
      System.out.println(msg);
  }

  // ########## STATE STACK ##########
  final static int YYSTACKSIZE = 500; // maximum stack size
  int statestk[] = new int[YYSTACKSIZE]; // state stack
  int stateptr;
  int stateptrmax; // highest index of stackptr
  int statemax; // state when highest index reached
  // ###############################################################
  // methods: state stack push,pop,drop,peek
  // ###############################################################

  final void state_push(int state) {
    try {
      stateptr++;
      statestk[stateptr] = state;
    } catch (ArrayIndexOutOfBoundsException e) {
      int oldsize = statestk.length;
      int newsize = oldsize * 2;
      int[] newstack = new int[newsize];
      System.arraycopy(statestk, 0, newstack, 0, oldsize);
      statestk = newstack;
      statestk[stateptr] = state;
    }
  }

  final int state_pop() {
    return statestk[stateptr--];
  }

  final void state_drop(int cnt) {
    stateptr -= cnt;
  }

  final int state_peek(int relative) {
    return statestk[stateptr - relative];
  }

  // ###############################################################
  // method: init_stacks : allocate and prepare stacks
  // ###############################################################
  final boolean init_stacks() {
    stateptr = -1;
    val_init();
    return true;
  }

  // ###############################################################
  // method: dump_stacks : show n levels of the stacks
  // ###############################################################
  void dump_stacks(int count) {
    int i;
    System.out.println("=index==state====value=     s:" + stateptr + "  v:" + valptr);
    for (i = 0; i < count; i++)
      System.out.println(" " + i + "    " + statestk[i] + "      " + valstk[i]);
    System.out.println("======================");
  }

  // ########## SEMANTIC VALUES ##########
  // public class ParserVal is defined in ParserVal.java

  String yytext;// user variable to return contextual strings
  ParserVal yyval; // used to return semantic vals from action routines
  ParserVal yylval;// the 'lval' (result) I got from yylex()
  ParserVal valstk[];
  int valptr;

  // ###############################################################
  // methods: value stack push,pop,drop,peek.
  // ###############################################################
  void val_init() {
    valstk = new ParserVal[YYSTACKSIZE];
    yyval = new ParserVal();
    yylval = new ParserVal();
    valptr = -1;
  }

  void val_push(ParserVal val) {
    if (valptr >= YYSTACKSIZE)
      return;
    valstk[++valptr] = val;
  }

  ParserVal val_pop() {
    if (valptr < 0)
      return new ParserVal();
    return valstk[valptr--];
  }

  void val_drop(int cnt) {
    int ptr;
    ptr = valptr - cnt;
    if (ptr < 0)
      return;
    valptr = ptr;
  }

  ParserVal val_peek(int relative) {
    int ptr;
    ptr = valptr - relative;
    if (ptr < 0)
      return new ParserVal();
    return valstk[ptr];
  }

  final ParserVal dup_yyval(ParserVal val) {
    ParserVal dup = new ParserVal();
    dup.ival = val.ival;
    dup.dval = val.dval;
    dup.sval = val.sval;
    dup.obj = val.obj;
    return dup;
  }

  // #### end semantic value section ####
  public final static short CLASS = 257;
  public final static short INTERFACE = 258;
  public final static short IMPLEMENT = 259;
  public final static short NEW = 260;
  public final static short SUPER = 261;
  public final static short THIS = 262;
  public final static short FUNC = 263;
  public final static short RETURN = 264;
  public final static short IF = 265;
  public final static short ELSE = 266;
  public final static short END_IF = 267;
  public final static short FOR = 268;
  public final static short IN = 269;
  public final static short RANGE = 270;
  public final static short IMPL = 271;
  public final static short PRINT = 272;
  public final static short TOD = 273;
  public final static short EQUAL_OPERATOR = 274;
  public final static short NOT_EQUAL_OPERATOR = 275;
  public final static short GREATER_THAN_OR_EQUAL_OPERATOR = 276;
  public final static short LESS_THAN_OR_EQUAL_OPERATOR = 277;
  public final static short MINUS_ASSIGN = 278;
  public final static short VOID = 279;
  public final static short LONG = 280;
  public final static short UINT = 281;
  public final static short DOUBLE = 282;
  public final static short BOOLEAN = 283;
  public final static short CADENA = 284;
  public final static short ID = 285;
  public final static short CTE_DOUBLE = 286;
  public final static short CTE_UINT = 287;
  public final static short CTE_LONG = 288;
  public final static short YYERRCODE = 256;
  final static short yylhs[] = { -1,
      0, 0, 1, 1, 2, 2, 2, 2, 3, 3,
      7, 9, 9, 10, 10, 11, 11, 13, 13, 16,
      16, 17, 17, 18, 19, 14, 21, 23, 23, 24,
      24, 22, 22, 25, 27, 12, 28, 8, 29, 29,
      5, 31, 31, 32, 32, 33, 4, 4, 34, 36,
      36, 35, 6, 6, 39, 40, 40, 41, 42, 43,
      44, 45, 45, 20, 49, 49, 49, 50, 50, 50,
      50, 50, 51, 51, 51, 52, 52, 52, 52, 52,
      53, 53, 54, 54, 55, 47, 48, 48, 46, 46,
      57, 57, 57, 56, 56, 56, 58, 58, 58, 60,
      60, 60, 60, 60, 60, 61, 61, 62, 62, 15,
      15, 63, 63, 38, 38, 59, 30, 64, 64, 66,
      66, 67, 65, 26, 26, 37, 37, 37, 68, 68,
      70, 70, 71, 73, 72, 72, 72, 72, 72, 72,
      74, 74, 74, 74, 80, 81, 81, 81, 79, 75,
      76, 77, 82, 83, 85, 84, 86, 86, 78, 78,
      69,
  };
  final static short yylen[] = { 2,
      1, 1, 1, 2, 1, 1, 1, 1, 3, 4,
      3, 1, 2, 1, 1, 1, 1, 3, 2, 1,
      3, 1, 3, 1, 1, 2, 2, 1, 1, 4,
      3, 1, 1, 2, 1, 2, 1, 2, 1, 3,
      3, 3, 3, 1, 2, 2, 3, 2, 2, 4,
      3, 1, 5, 4, 3, 1, 2, 1, 2, 1,
      3, 1, 1, 1, 1, 3, 3, 1, 3, 3,
      3, 3, 1, 3, 3, 1, 4, 3, 3, 3,
      1, 1, 1, 3, 1, 1, 3, 3, 1, 1,
      1, 1, 1, 1, 1, 1, 5, 4, 2, 5,
      4, 7, 6, 7, 6, 1, 1, 5, 4, 1,
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
      1, 1, 1, 3, 2, 4, 1, 3, 1, 2,
      1, 1, 2, 2, 1, 1, 1, 1, 1, 1,
      1, 1, 1, 1, 1, 1, 1, 1, 1, 7,
      9, 11, 1, 1, 1, 1, 1, 3, 3, 1,
      2,
  };
  final static short yydefred[] = { 0,
      0, 0, 0, 0, 0, 0, 0, 5, 6, 7,
      8, 0, 0, 29, 121, 120, 122, 113, 123, 28,
      0, 115, 0, 111, 114, 110, 112, 116, 118, 119,
      0, 4, 0, 0, 9, 0, 0, 41, 0, 49,
      0, 0, 47, 52, 127, 0, 0, 39, 117, 0,
      37, 0, 12, 14, 15, 16, 17, 0, 0, 0,
      0, 10, 0, 0, 0, 44, 0, 161, 0, 0,
      91, 0, 0, 0, 0, 0, 149, 139, 0, 141,
      146, 0, 62, 0, 0, 92, 147, 148, 0, 144,
      129, 131, 132, 0, 135, 136, 137, 138, 140, 142,
      143, 145, 0, 0, 19, 11, 13, 24, 0, 20,
      0, 33, 26, 32, 0, 27, 0, 36, 43, 46,
      42, 45, 51, 0, 0, 116, 0, 0, 153, 0,
      0, 0, 125, 0, 0, 90, 89, 0, 0, 128,
      0, 130, 107, 106, 133, 0, 53, 40, 18, 0,
      0, 0, 34, 50, 0, 0, 0, 86, 0, 0,
      0, 159, 0, 94, 95, 96, 0, 0, 35, 0,
      82, 0, 0, 0, 0, 76, 81, 83, 85, 124,
      61, 0, 126, 0, 0, 56, 58, 21, 23, 25,
      31, 0, 98, 0, 0, 0, 0, 0, 0, 0,
      0, 101, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 60, 59, 55, 57, 30,
      97, 0, 0, 88, 0, 87, 0, 0, 84, 100,
      0, 0, 0, 0, 0, 0, 0, 0, 78, 79,
      80, 0, 0, 105, 0, 0, 0, 154, 0, 77,
      103, 0, 104, 0, 150, 156, 0, 102, 0, 155,
      0, 151, 0, 0, 152,
  };
  final static short yydgoto[] = { 5,
      6, 7, 8, 9, 10, 11, 35, 36, 52, 53,
      54, 55, 56, 78, 20, 109, 110, 111, 189, 169,
      59, 113, 60, 116, 118, 80, 170, 61, 47, 22,
      38, 65, 66, 23, 43, 40, 44, 24, 147, 185,
      186, 187, 217, 81, 82, 138, 171, 84, 172, 173,
      174, 175, 176, 177, 178, 179, 85, 86, 25, 87,
      145, 88, 26, 27, 28, 29, 30, 89, 90, 91,
      92, 93, 94, 95, 96, 97, 98, 99, 100, 101,
      102, 130, 249, 257, 261, 0,
  };
  final static short yysindex[] = { -111,
      -237, -234, -148, -210, 0, -111, 0, 0, 0, 0,
      0, -109, -62, 0, 0, 0, 0, 0, 0, 0,
      -220, 0, -105, 0, 0, 0, 0, 0, 0, 0,
      -204, 0, -204, -166, 0, 19, -104, 0, 46, 0,
      80, 653, 0, 0, 0, 70, 66, 0, 0, 86,
      0, -113, 0, 0, 0, 0, 0, -141, -20, -136,
      -80, 0, 109, -136, -97, 0, 107, 0, -204, 105,
      0, 116, -124, -95, 131, 294, 0, 0, -141, 0,
      0, -52, 0, 0, 134, 0, 0, 0, 586, 0,
      0, 0, 0, -19, 0, 0, 0, 0, 0, 0,
      0, 0, 64, -204, 0, 0, 0, 0, 12, 0,
      130, 0, 0, 0, 158, 0, -141, 0, 0, 0,
      0, 0, 0, 163, 166, 0, -70, -194, 0, -53,
      -19, -34, 0, 623, 160, 0, 0, -40, -68, 0,
      96, 0, 0, 0, 0, -148, 0, 0, 0, -141,
      -40, 117, 0, 0, -8, 182, 177, 0, 183, 179,
      -29, 0, 187, 0, 0, 0, -40, -19, 0, 201,
      0, -201, -47, 9, 13, 0, 0, 0, 0, 0,
      0, 203, 0, -20, -86, 0, 0, 0, 0, 0,
      0, 208, 0, 209, -4, -41, 653, -30, 216, 6,
      226, 0, -19, -40, -40, -40, -40, -40, -40, -40,
      -40, 6, 6, 6, 2, 0, 0, 0, 0, 0,
      0, -19, 229, 0, -172, 0, -79, 230, 0, 0,
      -47, -47, 9, 9, 9, 9, 13, 13, 0, 0,
      0, -19, 232, 0, -19, 653, -19, 0, -79, 0,
      0, -19, 0, -10, 0, 0, -79, 0, -19, 0,
      244, 0, 653, -19, 0,
  };
  final static short yyrindex[] = { 0,
      0, 0, 0, 0, 0, 286, 1, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 3, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 173, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 558, -50, 0, 0, 0, 0, 0,
      0, 0, 0, -44, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 5, 0, 0, 0, 0, 0, 0, 0,
      -24, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 251, 0, 0, 0, 0, 0,
      0, 0, 0, 0, -18, 0, 0, 0, 0, 0,
      712, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, -42, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 681, 470, 324, 39, 0, 0, 0, 0, 0,
      0, -38, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      498, 528, 352, 380, 410, 438, 67, 95, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0,
  };
  final static short yygindex[] = { 0,
      0, 292, 0, 0, 0, 0, 266, 0, 0, 256,
      0, 0, 0, 7, 11, 231, 159, 195, 0, -63,
      -108, 133, 27, 261, -45, -43, -126, 0, 0, 14,
      0, 0, 265, 0, 0, 0, 0, 295, 0, 0,
      151, 0, 0, 212, 0, 0, -32, 0, 0, -99,
      4, -37, -91, 0, 0, -187, 217, 0, 268, 0,
      242, 0, 0, 0, 16, 0, 0, 277, 8, -55,
      0, -170, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0,
  };
  final static int YYTABLESIZE = 997;
  static short yytable[];
  static {
    yytable();
  }

  static void yytable() {
    yytable = new short[] { 167,
        2, 93, 48, 88, 54, 167, 168, 87, 137, 83,
        86, 106, 208, 34, 209, 114, 63, 42, 88, 22,
        63, 124, 87, 112, 144, 134, 225, 121, 194, 21,
        45, 167, 193, 142, 22, 167, 222, 184, 218, 248,
        57, 167, 242, 83, 58, 167, 48, 12, 49, 214,
        13, 210, 79, 211, 212, 149, 83, 31, 57, 213,
        37, 256, 58, 64, 39, 69, 157, 71, 223, 260,
        150, 117, 204, 205, 181, 254, 184, 117, 142, 73,
        19, 73, 73, 73, 126, 67, 79, 190, 243, 50,
        158, 64, 264, 246, 247, 83, 141, 73, 73, 79,
        73, 83, 76, 201, 231, 232, 192, 74, 228, 74,
        74, 74, 14, 15, 16, 17, 18, 148, 51, 49,
        239, 240, 241, 68, 104, 74, 74, 103, 74, 105,
        14, 15, 16, 17, 18, 75, 19, 75, 75, 75,
        114, 34, 50, 108, 79, 1, 2, 123, 115, 33,
        127, 3, 119, 75, 75, 128, 75, 191, 41, 4,
        129, 73, 117, 73, 83, 14, 15, 16, 17, 18,
        132, 51, 237, 238, 14, 15, 16, 17, 18, 139,
        19, 14, 15, 16, 17, 18, 146, 19, 131, 74,
        151, 74, 14, 15, 16, 17, 18, 152, 19, 15,
        16, 17, 18, 154, 19, 155, 164, 165, 166, 233,
        234, 235, 236, 83, 156, 161, 182, 75, 150, 75,
        183, 195, 196, 197, 198, 136, 200, 86, 206, 207,
        83, 22, 163, 63, 123, 88, 143, 134, 163, 87,
        199, 203, 215, 224, 158, 164, 165, 166, 220, 221,
        158, 164, 165, 166, 226, 227, 259, 3, 3, 48,
        48, 54, 54, 3, 163, 48, 229, 54, 163, 245,
        250, 3, 252, 48, 163, 54, 158, 164, 165, 166,
        158, 164, 165, 166, 263, 1, 158, 164, 165, 166,
        158, 164, 165, 166, 73, 38, 99, 32, 73, 73,
        73, 62, 73, 73, 73, 73, 73, 107, 188, 135,
        73, 153, 73, 73, 73, 73, 216, 73, 73, 73,
        73, 73, 74, 73, 120, 46, 74, 74, 74, 122,
        74, 74, 74, 74, 74, 219, 125, 77, 74, 159,
        74, 74, 74, 74, 160, 74, 74, 74, 74, 74,
        75, 74, 134, 0, 75, 75, 75, 0, 75, 75,
        75, 75, 75, 0, 68, 0, 75, 68, 75, 75,
        75, 75, 162, 75, 75, 75, 75, 75, 0, 75,
        0, 0, 68, 68, 0, 68, 15, 16, 17, 18,
        0, 19, 71, 0, 0, 71, 15, 16, 17, 18,
        0, 19, 0, 0, 0, 0, 0, 0, 0, 202,
        71, 71, 0, 71, 0, 0, 76, 0, 133, 0,
        72, 0, 0, 72, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 72, 72,
        0, 72, 0, 0, 230, 0, 68, 0, 68, 0,
        69, 0, 0, 69, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 244, 0, 0, 0, 0, 69, 69,
        0, 69, 0, 0, 71, 0, 71, 0, 70, 0,
        0, 70, 0, 251, 0, 0, 253, 0, 255, 0,
        0, 0, 0, 258, 0, 0, 70, 70, 0, 70,
        262, 0, 72, 0, 72, 265, 0, 0, 0, 0,
        65, 0, 0, 65, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 65, 0,
        0, 0, 69, 0, 69, 0, 0, 0, 66, 0,
        0, 66, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 69, 70, 71, 66, 41, 72, 0,
        70, 73, 70, 0, 0, 74, 0, 0, 67, 0,
        0, 67, 14, 15, 16, 17, 18, 0, 75, 68,
        0, 0, 0, 68, 68, 68, 67, 68, 68, 68,
        68, 68, 65, 0, 65, 68, 0, 68, 68, 68,
        68, 160, 68, 68, 68, 68, 68, 71, 68, 0,
        0, 71, 71, 71, 0, 71, 71, 71, 71, 71,
        66, 0, 66, 71, 0, 71, 71, 71, 71, 77,
        71, 71, 71, 71, 71, 72, 71, 0, 0, 72,
        72, 72, 0, 72, 72, 72, 72, 72, 0, 0,
        67, 72, 67, 72, 72, 72, 72, 0, 72, 72,
        72, 72, 72, 0, 72, 69, 77, 0, 0, 69,
        69, 69, 0, 69, 69, 69, 69, 69, 0, 0,
        160, 69, 160, 69, 69, 69, 69, 0, 69, 69,
        69, 69, 69, 70, 69, 0, 77, 70, 70, 70,
        0, 70, 70, 70, 70, 70, 0, 0, 76, 70,
        140, 70, 70, 70, 70, 0, 70, 70, 70, 70,
        70, 64, 70, 0, 64, 65, 0, 0, 0, 65,
        65, 65, 0, 65, 65, 65, 65, 65, 0, 64,
        0, 65, 0, 65, 65, 76, 0, 180, 65, 65,
        65, 65, 65, 66, 65, 144, 0, 66, 66, 66,
        0, 66, 66, 66, 66, 66, 0, 0, 0, 66,
        0, 66, 66, 0, 0, 76, 66, 66, 66, 66,
        66, 0, 66, 67, 0, 0, 0, 67, 67, 67,
        0, 67, 67, 67, 67, 67, 0, 0, 0, 67,
        0, 67, 67, 64, 0, 64, 67, 67, 67, 67,
        67, 0, 67, 160, 0, 0, 0, 160, 160, 160,
        0, 160, 160, 160, 160, 160, 0, 0, 0, 160,
        0, 0, 0, 0, 144, 0, 160, 160, 160, 160,
        160, 0, 160, 0, 0, 69, 70, 71, 0, 41,
        72, 0, 0, 73, 0, 0, 0, 74, 0, 0,
        0, 0, 0, 0, 14, 15, 16, 17, 18, 0,
        75, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 69, 70, 71, 0, 41, 72, 0, 0,
        73, 0, 0, 0, 74, 0, 0, 0, 0, 0,
        0, 14, 15, 16, 17, 18, 0, 75, 0, 0,
        0, 0, 69, 70, 71, 0, 41, 72, 0, 0,
        73, 0, 0, 0, 74, 0, 0, 0, 0, 0,
        0, 14, 15, 16, 17, 18, 64, 75, 0, 0,
        64, 64, 64, 0, 64, 64, 64, 64, 64, 0,
        0, 0, 64, 0, 0, 0, 0, 0, 0, 64,
        64, 64, 64, 64, 0, 64, 0, 0, 0, 0,
        0, 144, 144, 144, 0, 144, 144, 0, 0, 144,
        0, 0, 0, 144, 0, 0, 0, 0, 0, 0,
        144, 144, 144, 144, 144, 0, 144,
    };
  }

  static short yycheck[];
  static {
    yycheck();
  }

  static void yycheck() {
    yycheck = new short[] { 40,
        0, 46, 0, 46, 0, 40, 41, 46, 61, 42,
        61, 125, 60, 123, 62, 59, 61, 123, 61, 44,
        125, 67, 61, 44, 44, 44, 197, 125, 155, 3,
        23, 40, 41, 89, 59, 40, 41, 146, 125, 227,
        34, 40, 41, 76, 34, 40, 33, 285, 33, 37,
        285, 43, 42, 45, 42, 44, 89, 268, 52, 47,
        123, 249, 52, 37, 285, 260, 261, 262, 195, 257,
        59, 61, 274, 275, 138, 246, 185, 67, 134, 41,
        285, 43, 44, 45, 69, 40, 76, 151, 215, 256,
        285, 65, 263, 266, 267, 128, 89, 59, 60, 89,
        62, 134, 123, 167, 204, 205, 152, 41, 200, 43,
        44, 45, 279, 280, 281, 282, 283, 104, 285, 104,
        212, 213, 214, 44, 59, 59, 60, 58, 62, 44,
        279, 280, 281, 282, 283, 41, 285, 43, 44, 45,
        184, 123, 256, 285, 134, 257, 258, 41, 285, 259,
        46, 263, 44, 59, 60, 40, 62, 41, 264, 271,
        285, 123, 152, 125, 197, 279, 280, 281, 282, 283,
        40, 285, 210, 211, 279, 280, 281, 282, 283, 46,
        285, 279, 280, 281, 282, 283, 123, 285, 284, 123,
        61, 125, 279, 280, 281, 282, 283, 40, 285, 280,
        281, 282, 283, 41, 285, 40, 286, 287, 288, 206,
        207, 208, 209, 246, 285, 269, 285, 123, 59, 125,
        125, 40, 46, 41, 46, 278, 40, 278, 276, 277,
        263, 256, 273, 278, 285, 278, 256, 256, 273, 278,
        270, 41, 40, 285, 285, 286, 287, 288, 41, 41,
        285, 286, 287, 288, 285, 40, 267, 257, 258, 257,
        258, 257, 258, 263, 273, 263, 41, 263, 273, 41,
        41, 271, 41, 271, 273, 271, 285, 286, 287, 288,
        285, 286, 287, 288, 41, 0, 285, 286, 287, 288,
        285, 286, 287, 288, 256, 123, 46, 6, 260, 261,
        262, 36, 264, 265, 266, 267, 268, 52, 150, 79,
        272, 117, 274, 275, 276, 277, 184, 279, 280, 281,
        282, 283, 256, 285, 64, 31, 260, 261, 262, 65,
        264, 265, 266, 267, 268, 185, 69, 44, 272, 128,
        274, 275, 276, 277, 128, 279, 280, 281, 282, 283,
        256, 285, 76, -1, 260, 261, 262, -1, 264, 265,
        266, 267, 268, -1, 41, -1, 272, 44, 274, 275,
        276, 277, 131, 279, 280, 281, 282, 283, -1, 285,
        -1, -1, 59, 60, -1, 62, 280, 281, 282, 283,
        -1, 285, 41, -1, -1, 44, 280, 281, 282, 283,
        -1, 285, -1, -1, -1, -1, -1, -1, -1, 168,
        59, 60, -1, 62, -1, -1, 123, -1, 125, -1,
        41, -1, -1, 44, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, 59, 60,
        -1, 62, -1, -1, 203, -1, 123, -1, 125, -1,
        41, -1, -1, 44, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, 222, -1, -1, -1, -1, 59, 60,
        -1, 62, -1, -1, 123, -1, 125, -1, 41, -1,
        -1, 44, -1, 242, -1, -1, 245, -1, 247, -1,
        -1, -1, -1, 252, -1, -1, 59, 60, -1, 62,
        259, -1, 123, -1, 125, 264, -1, -1, -1, -1,
        41, -1, -1, 44, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, 59, -1,
        -1, -1, 123, -1, 125, -1, -1, -1, 41, -1,
        -1, 44, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, 260, 261, 262, 59, 264, 265, -1,
        123, 268, 125, -1, -1, 272, -1, -1, 41, -1,
        -1, 44, 279, 280, 281, 282, 283, -1, 285, 256,
        -1, -1, -1, 260, 261, 262, 59, 264, 265, 266,
        267, 268, 123, -1, 125, 272, -1, 274, 275, 276,
        277, 44, 279, 280, 281, 282, 283, 256, 285, -1,
        -1, 260, 261, 262, -1, 264, 265, 266, 267, 268,
        123, -1, 125, 272, -1, 274, 275, 276, 277, 44,
        279, 280, 281, 282, 283, 256, 285, -1, -1, 260,
        261, 262, -1, 264, 265, 266, 267, 268, -1, -1,
        123, 272, 125, 274, 275, 276, 277, -1, 279, 280,
        281, 282, 283, -1, 285, 256, 44, -1, -1, 260,
        261, 262, -1, 264, 265, 266, 267, 268, -1, -1,
        123, 272, 125, 274, 275, 276, 277, -1, 279, 280,
        281, 282, 283, 256, 285, -1, 44, 260, 261, 262,
        -1, 264, 265, 266, 267, 268, -1, -1, 123, 272,
        125, 274, 275, 276, 277, -1, 279, 280, 281, 282,
        283, 41, 285, -1, 44, 256, -1, -1, -1, 260,
        261, 262, -1, 264, 265, 266, 267, 268, -1, 59,
        -1, 272, -1, 274, 275, 123, -1, 125, 279, 280,
        281, 282, 283, 256, 285, 44, -1, 260, 261, 262,
        -1, 264, 265, 266, 267, 268, -1, -1, -1, 272,
        -1, 274, 275, -1, -1, 123, 279, 280, 281, 282,
        283, -1, 285, 256, -1, -1, -1, 260, 261, 262,
        -1, 264, 265, 266, 267, 268, -1, -1, -1, 272,
        -1, 274, 275, 123, -1, 125, 279, 280, 281, 282,
        283, -1, 285, 256, -1, -1, -1, 260, 261, 262,
        -1, 264, 265, 266, 267, 268, -1, -1, -1, 272,
        -1, -1, -1, -1, 123, -1, 279, 280, 281, 282,
        283, -1, 285, -1, -1, 260, 261, 262, -1, 264,
        265, -1, -1, 268, -1, -1, -1, 272, -1, -1,
        -1, -1, -1, -1, 279, 280, 281, 282, 283, -1,
        285, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, 260, 261, 262, -1, 264, 265, -1, -1,
        268, -1, -1, -1, 272, -1, -1, -1, -1, -1,
        -1, 279, 280, 281, 282, 283, -1, 285, -1, -1,
        -1, -1, 260, 261, 262, -1, 264, 265, -1, -1,
        268, -1, -1, -1, 272, -1, -1, -1, -1, -1,
        -1, 279, 280, 281, 282, 283, 256, 285, -1, -1,
        260, 261, 262, -1, 264, 265, 266, 267, 268, -1,
        -1, -1, 272, -1, -1, -1, -1, -1, -1, 279,
        280, 281, 282, 283, -1, 285, -1, -1, -1, -1,
        -1, 260, 261, 262, -1, 264, 265, -1, -1, 268,
        -1, -1, -1, 272, -1, -1, -1, -1, -1, -1,
        279, 280, 281, 282, 283, -1, 285,
    };
  }

  final static short YYFINAL = 5;
  final static short YYMAXTOKEN = 288;
  final static String yyname[] = {
      "end-of-file", null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, "'%'", null, null, "'('", "')'", "'*'", "'+'",
      "','", "'-'", "'.'", "'/'", null, null, null, null, null, null, null, null, null, null, "':'",
      "';'", "'<'", "'='", "'>'", null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, "'{'", null, "'}'", null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, "CLASS", "INTERFACE", "IMPLEMENT", "NEW",
      "SUPER", "THIS", "FUNC", "RETURN", "IF", "ELSE", "END_IF", "FOR", "IN", "RANGE", "IMPL",
      "PRINT", "TOD", "EQUAL_OPERATOR", "NOT_EQUAL_OPERATOR",
      "GREATER_THAN_OR_EQUAL_OPERATOR", "LESS_THAN_OR_EQUAL_OPERATOR", "MINUS_ASSIGN",
      "VOID", "LONG", "UINT", "DOUBLE", "BOOLEAN", "CADENA", "ID", "CTE_DOUBLE", "CTE_UINT",
      "CTE_LONG",
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
      "field_declaration : error ','",
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
      "real_parameter : expression",
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
      "assignment : left_hand_side assignment_operator expression",
      "left_hand_side : expression_name",
      "left_hand_side : field_access",
      "expression : equality_expression",
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
      "term : '(' expression ')'",
      "factor : literal",
      "expression_name : ID",
      "field_access : primary '.' ID",
      "field_access : SUPER '.' ID",
      "assignment_operator : '='",
      "assignment_operator : MINUS_ASSIGN",
      "primary : THIS",
      "primary : class_instance_creation_expression",
      "primary : field_access",
      "literal : CTE_DOUBLE",
      "literal : CTE_UINT",
      "literal : CTE_LONG",
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
      "invocation_end : error",
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
      "if_then_statement : IF '(' assignment ')' statement END_IF invocation_end",
      "if_then_else_statement : IF '(' assignment ')' statement ELSE statement END_IF invocation_end",
      "for_in_range_statement : FOR for_variable IN RANGE '(' for_init for_end for_update ')' statement invocation_end",
      "for_variable : ID",
      "for_init : literal",
      "for_update : literal",
      "for_end : literal",
      "statement_expression_list : statement_expression",
      "statement_expression_list : statement_expression_list ';' statement_expression",
      "print_statement : PRINT CADENA invocation_end",
      "print_statement : PRINT",
      "return_statement : RETURN ','",
  };

  // #line 411 "grammer.y"

  private static AnalizadorLexico aLexico;

  // This method is the one where BYACC/J expects to obtain its input tokens.
  // Wrap any file/string scanning code you have in this function. This method
  // should return <0 if there is an error, and 0 when it encounters the end of
  // input. See the examples to clarify what we mean.
  int yylex() {
    int token = -1;
    token = aLexico.generateToken();
    System.out.println("TOKEN: " + token);
    // yyval = new ParserVal(token); //genera la referencia a la tabla de simbolos?
    return token;
  }

  // This method is expected by BYACC/J, and is used to provide error messages to
  // be directed to the channels the user desires.
  void yyerror(String msg) {
    System.out.println(msg);
  }

  private static ArrayList<String> listFilesInDirectory(String path) {
    // Obtén el directorio actual
    File element = new File(System.getProperty("user.dir") + "/" + path);
    ArrayList<String> out = new ArrayList<>();

    // Verifica si es un directorio o archivo válido
    if (element.isDirectory() || element.isFile()) {
      // Lista de archivos y directorios en el directorio actual
      File[] filesAndDirs = element.listFiles();

      // Itera a través de los archivos y directorios
      int i = 0;
      for (File fileOrDir : filesAndDirs) {
        String name = fileOrDir.getName();
        System.out.println("[" + i + "]" + ": " + name);
        out.add(name);
        i++;
      }
    } else {
      System.err.println("No es un directorio válido.");
    }

    return out;
  }

  private static String generatePath() {
    ArrayList<String> directories = listFilesInDirectory("sample_programs");
    String path = "";

    if (!directories.isEmpty()) {
      Scanner scanner = new Scanner(System.in);
      int indice = -1;

      while (indice < 0) {
        System.out.print("Ingrese el numero de carpeta a acceder: ");
        String input = scanner.nextLine();
        indice = Integer.parseInt(input);
        if (indice < directories.size() || indice >= 0) {
          path = directories.get(indice);
          directories = listFilesInDirectory("sample_programs" + "/" + path);
        } else {
          System.out.println("El indice no es correcto, ingrese nuevamente...");
          indice = -1;
        }

      }

      if (!directories.isEmpty()) {
        indice = -1;

        while (indice < 0) {

          System.out.print("Ingrese el numero de archivo binario a compilar: ");
          String input = scanner.nextLine();
          indice = Integer.parseInt(input);

          if (indice < directories.size() || indice >= 0) {
            path += "/" + directories.get(Integer.parseInt(input));
          } else {

            System.out.println("El indice no es correcto, ingrese nuevamente...");
            indice = -1;
          }

        }
      }
      scanner.close();
    }
    return path;
  }

  public static void main(String[] args) throws ClassNotFoundException {
    System.out.println("Iniciando compilacion... ");

    String input = generatePath();

    aLexico = new AnalizadorLexico(input);

    if (!aLexico.hasReadWell()) {
      return;
    }

    Parser aSintactico = new Parser();

    // PalabrasReservadasTabla example = new PalabrasReservadasTabla(); // creo una
    // instancia para que se ejecute el
    // constructor y guarde todas las palabras en el
    // mapa estatico. Si no queda vacio
    System.out.println(PalabrasReservadasTabla.p.toString());

    // aSintactico.run();
    String listaTOKENS = "";
    for (int i = 0; i < 50; i++) {
      listaTOKENS = listaTOKENS + " " + aLexico.generateToken();
      System.out.println(listaTOKENS);
    }

    Logger.logError(1, "Este es un error.");
    Logger.logWarning(2, "Esta es una advertencia.");

    Logger.dumpLog();
    System.out.println(aLexico.getProgram());

  }

  // #line 727 "Parser.java"
  // ###############################################################
  // method: yylexdebug : check lexer state
  // ###############################################################
  void yylexdebug(int state, int ch) {
    String s = null;
    if (ch < 0)
      ch = 0;
    if (ch <= YYMAXTOKEN) // check index bounds
      s = yyname[ch]; // now get it
    if (s == null)
      s = "illegal-symbol";
    debug("state " + state + ", reading " + ch + " (" + s + ")");
  }

  // The following are now global, to aid in error reporting
  int yyn; // next next thing to do
  int yym; //
  int yystate; // current parsing state from state table
  String yys; // current token string

  // ###############################################################
  // method: yyparse : parse input and execute indicated items
  // ###############################################################
  int yyparse() {
    boolean doaction;
    init_stacks();
    yynerrs = 0;
    yyerrflag = 0;
    yychar = -1; // impossible char forces a read
    yystate = 0; // initial state
    state_push(yystate); // save it
    val_push(yylval); // save empty value
    while (true) // until parsing is done, either correctly, or w/error
    {
      doaction = true;
      if (yydebug)
        debug("loop");
      // #### NEXT ACTION (from reduction table)
      for (yyn = yydefred[yystate]; yyn == 0; yyn = yydefred[yystate]) {
        if (yydebug)
          debug("yyn:" + yyn + "  state:" + yystate + "  yychar:" + yychar);
        if (yychar < 0) // we want a char?
        {
          yychar = yylex(); // get next token
          if (yydebug)
            debug(" next yychar:" + yychar);
          // #### ERROR CHECK ####
          if (yychar < 0) // it it didn't work/error
          {
            yychar = 0; // change it to default string (no -1!)
            if (yydebug)
              yylexdebug(yystate, yychar);
          }
        } // yychar<0
        yyn = yysindex[yystate]; // get amount to shift by (shift index)
        if ((yyn != 0) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar) {
          if (yydebug)
            debug("state " + yystate + ", shifting to state " + yytable[yyn]);
          // #### NEXT STATE ####
          yystate = yytable[yyn];// we are in a new state
          state_push(yystate); // save it
          val_push(yylval); // push our lval as the input for next rule
          yychar = -1; // since we have 'eaten' a token, say we need another
          if (yyerrflag > 0) // have we recovered an error?
            --yyerrflag; // give ourselves credit
          doaction = false; // but don't process yet
          break; // quit the yyn=0 loop
        }

        yyn = yyrindex[yystate]; // reduce
        if ((yyn != 0) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar) { // we reduced!
          if (yydebug)
            debug("reduce");
          yyn = yytable[yyn];
          doaction = true; // get ready to execute
          break; // drop down to actions
        } else // ERROR RECOVERY
        {
          if (yyerrflag == 0) {
            yyerror("syntax error");
            yynerrs++;
          }
          if (yyerrflag < 3) // low error count?
          {
            yyerrflag = 3;
            while (true) // do until break
            {
              if (stateptr < 0) // check for under & overflow here
              {
                yyerror("stack underflow. aborting..."); // note lower case 's'
                return 1;
              }
              yyn = yysindex[state_peek(0)];
              if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                  yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE) {
                if (yydebug)
                  debug("state " + state_peek(0) + ", error recovery shifting to state " + yytable[yyn] + " ");
                yystate = yytable[yyn];
                state_push(yystate);
                val_push(yylval);
                doaction = false;
                break;
              } else {
                if (yydebug)
                  debug("error recovery discarding state " + state_peek(0) + " ");
                if (stateptr < 0) // check for under & overflow here
                {
                  yyerror("Stack underflow. aborting..."); // capital 'S'
                  return 1;
                }
                state_pop();
                val_pop();
              }
            }
          } else // discard this token
          {
            if (yychar == 0)
              return 1; // yyabort
            if (yydebug) {
              yys = null;
              if (yychar <= YYMAXTOKEN)
                yys = yyname[yychar];
              if (yys == null)
                yys = "illegal-symbol";
              debug("state " + yystate + ", error recovery discards token " + yychar + " (" + yys + ")");
            }
            yychar = -1; // read another
          }
        } // end error recovery
      } // yyn=0 loop
      if (!doaction) // any reason not to proceed?
        continue; // skip action
      yym = yylen[yyn]; // get count of terminals on rhs
      if (yydebug)
        debug("state " + yystate + ", reducing " + yym + " by rule " + yyn + " (" + yyrule[yyn] + ")");
      if (yym > 0) // if count of rhs not 'nil'
        yyval = val_peek(yym - 1); // get current semantic value
      yyval = dup_yyval(yyval); // duplicate yyval if ParserVal is used as semantic value
      switch (yyn) {
        // ########## USER-SUPPLIED ACTIONS ##########
        case 48:
        // #line 149 "grammer.y"
        {
          Logger.logError(0, "Es necesario implementar el cuerpo de la funcion.");
        }
          break;
        case 54:
        // #line 163 "grammer.y"
        {
          Logger.logError(0, "Es necesario implementar el cuerpo del metodo.");
        }
          break;
        case 99:
        // #line 255 "grammer.y"
        {
          Logger.logError(0, "Se esperaba un \'(\'.");
        }
          break;
        case 107:
        // #line 269 "grammer.y"
        {
          Logger.logError(0, "Se esperaba una \",\".");
        }
          break;
        case 128:
        // #line 329 "grammer.y"
        {
          Logger.logError(0, "Es necesario declarar el returno de la funcion.");
        }
          break;
        case 160:
        // #line 402 "grammer.y"
        {
          Logger.logError(0, "Se esperaba una cadena.");
        }
          break;
        // #line 900 "Parser.java"
        // ########## END OF USER-SUPPLIED ACTIONS ##########
      }// switch
       // #### Now let's reduce... ####
      if (yydebug)
        debug("reduce");
      state_drop(yym); // we just reduced yylen states
      yystate = state_peek(0); // get new state
      val_drop(yym); // corresponding value drop
      yym = yylhs[yyn]; // select next TERMINAL(on lhs)
      if (yystate == 0 && yym == 0)// done? 'rest' state and at first TERMINAL
      {
        if (yydebug)
          debug("After reduction, shifting from state 0 to state " + YYFINAL + "");
        yystate = YYFINAL; // explicitly say we're done
        state_push(YYFINAL); // and save it
        val_push(yyval); // also save the semantic value of parsing
        if (yychar < 0) // we want another character?
        {
          yychar = yylex(); // get next character
          if (yychar < 0)
            yychar = 0; // clean, if necessary
          if (yydebug)
            yylexdebug(yystate, yychar);
        }
        if (yychar == 0) // Good exit (if lex returns 0 ;-)
          break; // quit the loop--all DONE
      } // if yystate
      else // else not done yet
      { // get next state and push, for next yydefred[]
        yyn = yygindex[yym]; // find out where to go
        if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
          yystate = yytable[yyn]; // get new state
        else
          yystate = yydgoto[yym]; // else go to new defred
        if (yydebug)
          debug("after reduction, shifting from state " + state_peek(0) + " to state " + yystate + "");
        state_push(yystate); // going again, so push state & val...
        val_push(yyval); // for next action
      }
    } // main loop
    return 0;// yyaccept!!
  }
  // ## end of method parse() ######################################

  // ## run() --- for Thread #######################################
  /**
   * A default run method, used for operating this parser
   * object in the background. It is intended for extending Thread
   * or implementing Runnable. Turn off with -Jnorun .
   */
  public void run() {
    yyparse();
  }
  // ## end of method run() ########################################

  // ## Constructors ###############################################
  /**
   * Default constructor. Turn off with -Jnoconstruct .
   * 
   */
  public Parser() {
    // nothing to do
  }

  /**
   * Create a parser, setting the debug to true or false.
   * 
   * @param debugMe true for debugging, false for no debug.
   */
  public Parser(boolean debugMe) {
    yydebug = debugMe;
  }
  // ###############################################################

}
// ################### END OF CLASS ##############################
