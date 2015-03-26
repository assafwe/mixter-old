﻿namespace Mixter.Domain.Subscriptions
{
    public struct SubscriptionId
    {
        public UserId Follower { get; private set; }

        public UserId Followee { get; private set; }

        public SubscriptionId(UserId follower, UserId followee)
            : this()
        {
            Follower = follower;
            Followee = followee;
        }
    }
}