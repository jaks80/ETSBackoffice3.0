http://howtodoinjava.com/2013/08/03/jax-rs-2-0-resteasy-3-0-2-final-client-api-example/

Task: tab listener


<plugin>
                <groupId>org.codehaus.mojo.webstart</groupId>
                <artifactId>webstart-maven-plugin</artifactId>
                <executions>
                    <execution>                        
                        <phase>package</phase>
                        <goals>
                            <goal>jnlp-inline</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <!--outputDirectory></outputDirectory-->

                    <!-- Set to true to exclude all transitive dependencies. Default is false. -->
                    <excludeTransitive>false</excludeTransitive>
                    <outputJarVersions>true</outputJarVersions>
                    <!-- JNLP generation -->
                    <jnlp>
                        <!-- default values -->
                        <!--inputTemplateResourcePath>${project.basedir}</inputTemplateResourcePath-->
                        <inputTemplate>src/main/jnlp/template.vm</inputTemplate>
                        <outputFile>app.jnlp</outputFile> <!-- defaults to launch.jnlp -->
                        <mainClass>com.ets.fe.a_main.DlgLogin</mainClass>
                    </jnlp>


                    <!-- SIGNING -->
                    <!-- defining this will automatically sign the jar and its dependencies, if necessary -->
                    <sign>
                        <keystore>${basedir}/keystore</keystore>
                        <keypass>ets215</keypass>  <!-- we need to override passwords easily from the command line. ${keypass} -->
                        <storepass>ets215</storepass> <!-- ${storepass} -->
                        <storetype>JKS</storetype>
                        <alias>EmbeddedTomcatWebstart</alias>

                        <!--validity>fillme</validity-->

                        <!-- only required for generating the keystore -->
                        <dnameCn>EmbeddedTomcatWebstart</dnameCn>
                        <dnameOu>ETS</dnameOu>
                        <dnameO>Organisation</dnameO>
                        <dnameL>London</dnameL>
                        <dnameSt>London</dnameSt>
                        <dnameC>GB</dnameC>

                        <verify>true</verify> <!-- verify that the signing operation succeeded -->

                        <!-- KEYSTORE MANAGEMENT -->
                        <keystoreConfig>
                            <delete>true</delete> <!-- delete the keystore -->
                            <gen>true</gen>       <!-- optional shortcut to generate the store. -->
                        </keystoreConfig>
                    </sign>

                    <!-- BUILDING PROCESS -->

                    <pack200>false</pack200>
                    <gzip>true</gzip> <!-- default force when pack200 false, true when pack200 selected ?? -->

                    <!-- causes a version attribute to be output in each jar resource element, optional, default is false -->
                    <outputJarVersions>false</outputJarVersions>
                    <verbose>true</verbose>
                </configuration>
            </plugin>
            