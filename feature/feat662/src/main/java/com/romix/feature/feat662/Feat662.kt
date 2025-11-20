package com.romix.feature.feat662

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat662Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat662UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat662FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat662UserSummary
)

data class Feat662UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat662NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat662Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat662Config = Feat662Config()
) {

    fun loadSnapshot(userId: Long): Feat662NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat662NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat662UserSummary {
        return Feat662UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat662FeedItem> {
        val result = java.util.ArrayList<Feat662FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat662FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat662UiMapper {

    fun mapToUi(model: List<Feat662FeedItem>): Feat662UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat662UiModel(
            header = UiText("Feat662 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat662UiModel =
        Feat662UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat662UiModel =
        Feat662UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat662UiModel =
        Feat662UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat662Service(
    private val repository: Feat662Repository,
    private val uiMapper: Feat662UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat662UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat662UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat662UserItem1(val user: CoreUser, val label: String)
data class Feat662UserItem2(val user: CoreUser, val label: String)
data class Feat662UserItem3(val user: CoreUser, val label: String)
data class Feat662UserItem4(val user: CoreUser, val label: String)
data class Feat662UserItem5(val user: CoreUser, val label: String)
data class Feat662UserItem6(val user: CoreUser, val label: String)
data class Feat662UserItem7(val user: CoreUser, val label: String)
data class Feat662UserItem8(val user: CoreUser, val label: String)
data class Feat662UserItem9(val user: CoreUser, val label: String)
data class Feat662UserItem10(val user: CoreUser, val label: String)

data class Feat662StateBlock1(val state: Feat662UiModel, val checksum: Int)
data class Feat662StateBlock2(val state: Feat662UiModel, val checksum: Int)
data class Feat662StateBlock3(val state: Feat662UiModel, val checksum: Int)
data class Feat662StateBlock4(val state: Feat662UiModel, val checksum: Int)
data class Feat662StateBlock5(val state: Feat662UiModel, val checksum: Int)
data class Feat662StateBlock6(val state: Feat662UiModel, val checksum: Int)
data class Feat662StateBlock7(val state: Feat662UiModel, val checksum: Int)
data class Feat662StateBlock8(val state: Feat662UiModel, val checksum: Int)
data class Feat662StateBlock9(val state: Feat662UiModel, val checksum: Int)
data class Feat662StateBlock10(val state: Feat662UiModel, val checksum: Int)

fun buildFeat662UserItem(user: CoreUser, index: Int): Feat662UserItem1 {
    return Feat662UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat662StateBlock(model: Feat662UiModel): Feat662StateBlock1 {
    return Feat662StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat662UserSummary> {
    val list = java.util.ArrayList<Feat662UserSummary>(users.size)
    for (user in users) {
        list += Feat662UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat662UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat662UiModel {
    val summaries = (0 until count).map {
        Feat662UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat662UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat662UiModel> {
    val models = java.util.ArrayList<Feat662UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat662AnalyticsEvent1(val name: String, val value: String)
data class Feat662AnalyticsEvent2(val name: String, val value: String)
data class Feat662AnalyticsEvent3(val name: String, val value: String)
data class Feat662AnalyticsEvent4(val name: String, val value: String)
data class Feat662AnalyticsEvent5(val name: String, val value: String)
data class Feat662AnalyticsEvent6(val name: String, val value: String)
data class Feat662AnalyticsEvent7(val name: String, val value: String)
data class Feat662AnalyticsEvent8(val name: String, val value: String)
data class Feat662AnalyticsEvent9(val name: String, val value: String)
data class Feat662AnalyticsEvent10(val name: String, val value: String)

fun logFeat662Event1(event: Feat662AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat662Event2(event: Feat662AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat662Event3(event: Feat662AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat662Event4(event: Feat662AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat662Event5(event: Feat662AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat662Event6(event: Feat662AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat662Event7(event: Feat662AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat662Event8(event: Feat662AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat662Event9(event: Feat662AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat662Event10(event: Feat662AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat662Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat662Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat662Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat662Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat662Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat662Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat662Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat662Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat662Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat662Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat662(u: CoreUser): Feat662Projection1 =
    Feat662Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat662Projection1> {
    val list = java.util.ArrayList<Feat662Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat662(u)
    }
    return list
}
