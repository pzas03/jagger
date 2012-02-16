/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.griddynamics.jagger.facade;

import com.griddynamics.jagger.facade.client.navigation.LocationDTO;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * User: dkotlyarov
 */
@Root(name = "config")
public class Config {
    @Attribute(name = "content_directory")
    private String contentDirectory;

    @ElementList(name = "locations", entry = "location", inline = true)
    private ArrayList<Location> locations;

    public Config() {
    }

    public Config(String contentDirectory, ArrayList<Location> locations) {
        this.contentDirectory = contentDirectory;
        this.locations = locations;
    }

    public String getContentDirectory() {
        if (!contentDirectory.endsWith("/")) {
            contentDirectory += "/";
        }
        return contentDirectory;
    }

    public void setContentDirectory(String contentDirectory) {
        this.contentDirectory = contentDirectory;
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    public static class Location {
        @Attribute(name = "name")
        private String name;

        @Attribute(name = "directory")
        private String directory;

        @Element(name = "start_session")
        private StartSession startSession;

        public Location() {
        }

        public Location(String name, String directory) {
            this.name = name;
            this.directory = directory;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDirectory() {
            if (!directory.endsWith("/")) {
                directory += "/";
            }
            return directory;
        }

        public void setDirectory(String directory) {
            this.directory = directory;
        }

        public StartSession getStartSession() {
            return startSession;
        }

        public void setStartSession(StartSession startSession) {
            this.startSession = startSession;
        }

        public LocationDTO toDTO() {
            return new LocationDTO(name, directory);
        }

        @Override
        public String toString() {
            return "Location{" +
                    "name='" + name + '\'' +
                    ", directory='" + directory + '\'' +
                    '}';
        }

        public static LocationDTO[] toDTOs(Collection<Config.Location> locations) {
            LocationDTO[] locationDTOs = new LocationDTO[locations.size()];
            int i = 0;
            for (Config.Location location : locations) {
                locationDTOs[i++] = location.toDTO();
            }
            return locationDTOs;
        }

        public static class StartSession {
            @Attribute(name = "cmd")
            private String cmd;

            public StartSession() {
            }

            public StartSession(String cmd) {
                this.cmd = cmd;
            }

            public String getCmd() {
                return cmd;
            }

            public void setCmd(String cmd) {
                this.cmd = cmd;
            }

            @Override
            public String toString() {
                return "StartSession{" +
                        "cmd='" + cmd + '\'' +
                        '}';
            }
        }
    }
}
