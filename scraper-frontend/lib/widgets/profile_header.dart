import 'package:flutter/material.dart';

import '../models/linkedin_profile.dart';

class ProfileHeader extends StatelessWidget {
  final HybridLinkedInProfileResponse profile;

  const ProfileHeader({super.key, required this.profile});

  @override
  Widget build(BuildContext context) {
    final imageUrl = profile.profilePhotoUrl;

    return Card(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _coverImage(profile.coverImageUrl),
          Padding(
            padding: const EdgeInsets.all(16),
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                CircleAvatar(
                  radius: 36,
                  backgroundImage: imageUrl != null && imageUrl.isNotEmpty
                      ? NetworkImage(imageUrl)
                      : null,
                  child: imageUrl == null || imageUrl.isEmpty
                      ? const Icon(Icons.person, size: 36)
                      : null,
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text('Profile',
                          style: Theme.of(context).textTheme.titleLarge),
                      const SizedBox(height: 8),
                      _field('Full Name', profile.fullName),
                      _field('Headline', profile.headline),
                      _field('Description', profile.description),
                      _field('Profile URL', profile.profileUrl),
                      _field('Vanity URL', profile.vanityUrl),
                      _field('Location', profile.location),
                      _field('Country', profile.countryCode),
                      _field(
                          'Follower Count', profile.followerCount?.toString()),
                      _field('Connection Count', profile.connectionCount),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _field(String label, String? value) {
    final displayValue = value ?? 'null';
    return Padding(
      padding: const EdgeInsets.only(bottom: 4),
      child: RichText(
        text: TextSpan(
          style: const TextStyle(color: Colors.black87),
          children: [
            TextSpan(
              text: '$label: ',
              style: const TextStyle(fontWeight: FontWeight.w600),
            ),
            TextSpan(text: displayValue),
          ],
        ),
      ),
    );
  }

  Widget _coverImage(String? imageUrl) {
    if (imageUrl == null) {
      return const Padding(
        padding: EdgeInsets.all(16),
        child: Text('null'),
      );
    }

    return ClipRRect(
      borderRadius: const BorderRadius.vertical(top: Radius.circular(12)),
      child: Image.network(
        imageUrl,
        width: double.infinity,
        height: 180,
        fit: BoxFit.cover,
        errorBuilder: (_, __, ___) => const Padding(
          padding: EdgeInsets.all(16),
          child: Text('null'),
        ),
      ),
    );
  }
}
