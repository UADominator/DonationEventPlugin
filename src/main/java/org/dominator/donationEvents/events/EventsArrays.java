package org.dominator.donationEvents.events;

import java.util.ArrayList;
import java.util.List;

public class EventsArrays {
    public Events events;

    public boolean inRangePrice(double priceOth){
        return events.price.startSum <= priceOth && priceOth <= events.price.endSum;
    }

    public static  class Events {
        public String eventName;
        public Price price;
        public List<Commands> commands = new ArrayList<>();

        public Events(List<Commands> commands, Price price, String eventName) {
            this.commands = commands;
            this.price = price;
            this.eventName = eventName;
        }

        public Events(Price price) {
            this.price = price;
        }

        public static class Commands {
            public Type type;
            public String command;
            public String addition;

            public Commands(String command, String addition, Type type) {
                this.command = command;
                this.addition = addition;
                this.type = type;
            }

            public Commands(String command) {
                this(command, "", Type.STRING_CORDS_STRING);
            }
        }


        public static  class Price {
            double startSum;
            double endSum;

            public Price(double startSum, double endSum) {
                this.startSum = startSum;
                this.endSum = endSum;
            }
        }

        public static enum Type{
            STRING_CORDS_STRING,
            STRING_PLAYER_STRING,
            STRING_PLAYER_CORDS_STRING,
            CUSTOM_EVENT
        }
    }
}
