package com.romix.feature.feat21

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat21Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat21UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat21FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat21UserSummary
)

data class Feat21UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat21NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat21Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat21Config = Feat21Config()
) {

    fun loadSnapshot(userId: Long): Feat21NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat21NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat21UserSummary {
        return Feat21UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat21FeedItem> {
        val result = java.util.ArrayList<Feat21FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat21FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat21UiMapper {

    fun mapToUi(model: List<Feat21FeedItem>): Feat21UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat21UiModel(
            header = UiText("Feat21 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat21UiModel =
        Feat21UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat21UiModel =
        Feat21UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat21UiModel =
        Feat21UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat21Service(
    private val repository: Feat21Repository,
    private val uiMapper: Feat21UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat21UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat21UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat21UserItem1(val user: CoreUser, val label: String)
data class Feat21UserItem2(val user: CoreUser, val label: String)
data class Feat21UserItem3(val user: CoreUser, val label: String)
data class Feat21UserItem4(val user: CoreUser, val label: String)
data class Feat21UserItem5(val user: CoreUser, val label: String)
data class Feat21UserItem6(val user: CoreUser, val label: String)
data class Feat21UserItem7(val user: CoreUser, val label: String)
data class Feat21UserItem8(val user: CoreUser, val label: String)
data class Feat21UserItem9(val user: CoreUser, val label: String)
data class Feat21UserItem10(val user: CoreUser, val label: String)

data class Feat21StateBlock1(val state: Feat21UiModel, val checksum: Int)
data class Feat21StateBlock2(val state: Feat21UiModel, val checksum: Int)
data class Feat21StateBlock3(val state: Feat21UiModel, val checksum: Int)
data class Feat21StateBlock4(val state: Feat21UiModel, val checksum: Int)
data class Feat21StateBlock5(val state: Feat21UiModel, val checksum: Int)
data class Feat21StateBlock6(val state: Feat21UiModel, val checksum: Int)
data class Feat21StateBlock7(val state: Feat21UiModel, val checksum: Int)
data class Feat21StateBlock8(val state: Feat21UiModel, val checksum: Int)
data class Feat21StateBlock9(val state: Feat21UiModel, val checksum: Int)
data class Feat21StateBlock10(val state: Feat21UiModel, val checksum: Int)

fun buildFeat21UserItem(user: CoreUser, index: Int): Feat21UserItem1 {
    return Feat21UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat21StateBlock(model: Feat21UiModel): Feat21StateBlock1 {
    return Feat21StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat21UserSummary> {
    val list = java.util.ArrayList<Feat21UserSummary>(users.size)
    for (user in users) {
        list += Feat21UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat21UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat21UiModel {
    val summaries = (0 until count).map {
        Feat21UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat21UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat21UiModel> {
    val models = java.util.ArrayList<Feat21UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat21AnalyticsEvent1(val name: String, val value: String)
data class Feat21AnalyticsEvent2(val name: String, val value: String)
data class Feat21AnalyticsEvent3(val name: String, val value: String)
data class Feat21AnalyticsEvent4(val name: String, val value: String)
data class Feat21AnalyticsEvent5(val name: String, val value: String)
data class Feat21AnalyticsEvent6(val name: String, val value: String)
data class Feat21AnalyticsEvent7(val name: String, val value: String)
data class Feat21AnalyticsEvent8(val name: String, val value: String)
data class Feat21AnalyticsEvent9(val name: String, val value: String)
data class Feat21AnalyticsEvent10(val name: String, val value: String)

fun logFeat21Event1(event: Feat21AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat21Event2(event: Feat21AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat21Event3(event: Feat21AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat21Event4(event: Feat21AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat21Event5(event: Feat21AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat21Event6(event: Feat21AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat21Event7(event: Feat21AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat21Event8(event: Feat21AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat21Event9(event: Feat21AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat21Event10(event: Feat21AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat21Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat21Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat21Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat21Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat21Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat21Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat21Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat21Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat21Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat21Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat21(u: CoreUser): Feat21Projection1 =
    Feat21Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat21Projection1> {
    val list = java.util.ArrayList<Feat21Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat21(u)
    }
    return list
}
