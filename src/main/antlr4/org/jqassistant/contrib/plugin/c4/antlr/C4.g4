grammar C4;

c4:
    Startuml NL+
    Include C4Component NL+
    element*
    Enduml NL*
    EOF;

element: ((addProperty NL+)* ((c4Element hierarchy?) | rel) NL+);
addProperty: AddProperty LB key=Param CM value=Param RB;

c4Element: component | container | system;
hierarchy: (RCB NL* element* LCB);
component returns [org.jqassistant.contrib.plugin.c4.data.SecondaryElementType secondaryType, boolean external]
    : (Component { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.DEFAULT; }
    | ComponentExt { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.DEFAULT; $external = true; }
    | ComponentDb { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.DB; }
    | ComponentDbExt { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.DB; $external = true; }
    | ComponentQueue { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.QUEUE; }
    | ComponentQueueExt { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.QUEUE; $external = true; })
    paramList;

container returns [org.jqassistant.contrib.plugin.c4.data.SecondaryElementType secondaryType, boolean external]
    : (Container { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.DEFAULT; }
    | ContainerExt { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.DEFAULT; $external = true; }
    | ContainerDb { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.DB; }
    | ContainerDbExt { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.DB; $external = true; }
    | ContainerQueue { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.QUEUE; }
    | ContainerQueueExt { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.QUEUE; $external = true; })
    paramList;

system returns [org.jqassistant.contrib.plugin.c4.data.SecondaryElementType secondaryType, boolean external]
    : (System { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.DEFAULT; }
    | SystemExt { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.DEFAULT; $external = true; }
    | SystemDb { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.DB; }
    | SystemDbExt { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.DB; $external = true; }
    | SystemQueue { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.QUEUE; }
    | SystemQueueExt { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.QUEUE; $external = true; })
    systemParamList;

paramList: LB alias=Param CM label=Param CM tech=Param (CM opt1=Param (CM opt2=Param (CM opt3=Param (CM opt4=Param)?)?)?)? RB;
systemParamList: LB alias=Param CM label=Param (CM opt1=Param (CM opt2=Param (CM opt3=Param (CM opt4=Param)?)?)?)? RB;
elementBoundaryParamList: LB alias=Param CM label=Param (CM opt1=Param (CM opt2=Param)?)? RB;
rel: Rel relParamList;
relParamList: LB from=Param CM to=Param CM label=Param (CM opt1=Param (CM opt2=Param (CM opt3=Param(CM opt4=Param (CM opt5=Param)?)?)?))? RB;

Startuml: '@startuml';
Enduml: '@enduml';
Include: '!include';
C4Component: '<C4/C4_Component>';
AddProperty: 'AddProperty';
Component: 'Component';
ComponentExt: Component Ext;
ComponentDb: Component DB;
ComponentDbExt: ComponentDb Ext;
ComponentQueue: Component Queue;
ComponentQueueExt: ComponentQueue Ext;
Container: 'Container';
ContainerExt: Container Ext;
ContainerDb: Container DB;
ContainerDbExt: ContainerDb Ext;
ContainerQueue: Container Queue;
ContainerQueueExt: ContainerQueue Ext;
CountainerBoundary: Container US Boundary;
System: 'System';
SystemExt: System Ext;
SystemDb: System DB;
SystemDbExt: SystemDb Ext;
SystemQueue: System Queue;
SystemQueueExt: System Queue Ext;
SystemBoundary: System US Boundary;
EnterpriseBoundary: Enterprise Boundary;
Enterprise: 'Enterprise';
Boundary: 'Boundary';
DB: 'Db';
Queue: 'Queue';
Ext: '_Ext';
Rel: 'Rel'| 'Rel_U' | 'Rel_Up' | 'Rel_D' | 'Rel_Down' | 'Rel_L' | 'Rel_Left' | 'Rel_R' | 'Rel_Right';
Param: '"' String '"' | String | Int | KeyValue;
KeyValue: '$' String '=' (String | '"' String '"');
String: ('A'..'Z' | 'a'..'z' | '_' | Int)+;
Int: ('0'..'9')+;

CM: ',';
RCB: '{';
LCB: '}';
LB: '(';
RB: ')';
US: '_';
WS: (' ' | '\t')+ -> skip;
NL:  '\r'? '\n';