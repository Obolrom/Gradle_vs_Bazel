package com.romix.feature.feat135

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat135Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat135UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat135FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat135UserSummary
)

data class Feat135UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat135NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat135Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat135Config = Feat135Config()
) {

    fun loadSnapshot(userId: Long): Feat135NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat135NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat135UserSummary {
        return Feat135UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat135FeedItem> {
        val result = java.util.ArrayList<Feat135FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat135FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat135UiMapper {

    fun mapToUi(model: List<Feat135FeedItem>): Feat135UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat135UiModel(
            header = UiText("Feat135 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat135UiModel =
        Feat135UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat135UiModel =
        Feat135UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat135UiModel =
        Feat135UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat135Service(
    private val repository: Feat135Repository,
    private val uiMapper: Feat135UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat135UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat135UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat135UserItem1(val user: CoreUser, val label: String)
data class Feat135UserItem2(val user: CoreUser, val label: String)
data class Feat135UserItem3(val user: CoreUser, val label: String)
data class Feat135UserItem4(val user: CoreUser, val label: String)
data class Feat135UserItem5(val user: CoreUser, val label: String)
data class Feat135UserItem6(val user: CoreUser, val label: String)
data class Feat135UserItem7(val user: CoreUser, val label: String)
data class Feat135UserItem8(val user: CoreUser, val label: String)
data class Feat135UserItem9(val user: CoreUser, val label: String)
data class Feat135UserItem10(val user: CoreUser, val label: String)

data class Feat135StateBlock1(val state: Feat135UiModel, val checksum: Int)
data class Feat135StateBlock2(val state: Feat135UiModel, val checksum: Int)
data class Feat135StateBlock3(val state: Feat135UiModel, val checksum: Int)
data class Feat135StateBlock4(val state: Feat135UiModel, val checksum: Int)
data class Feat135StateBlock5(val state: Feat135UiModel, val checksum: Int)
data class Feat135StateBlock6(val state: Feat135UiModel, val checksum: Int)
data class Feat135StateBlock7(val state: Feat135UiModel, val checksum: Int)
data class Feat135StateBlock8(val state: Feat135UiModel, val checksum: Int)
data class Feat135StateBlock9(val state: Feat135UiModel, val checksum: Int)
data class Feat135StateBlock10(val state: Feat135UiModel, val checksum: Int)

fun buildFeat135UserItem(user: CoreUser, index: Int): Feat135UserItem1 {
    return Feat135UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat135StateBlock(model: Feat135UiModel): Feat135StateBlock1 {
    return Feat135StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat135UserSummary> {
    val list = java.util.ArrayList<Feat135UserSummary>(users.size)
    for (user in users) {
        list += Feat135UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat135UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat135UiModel {
    val summaries = (0 until count).map {
        Feat135UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat135UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat135UiModel> {
    val models = java.util.ArrayList<Feat135UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat135AnalyticsEvent1(val name: String, val value: String)
data class Feat135AnalyticsEvent2(val name: String, val value: String)
data class Feat135AnalyticsEvent3(val name: String, val value: String)
data class Feat135AnalyticsEvent4(val name: String, val value: String)
data class Feat135AnalyticsEvent5(val name: String, val value: String)
data class Feat135AnalyticsEvent6(val name: String, val value: String)
data class Feat135AnalyticsEvent7(val name: String, val value: String)
data class Feat135AnalyticsEvent8(val name: String, val value: String)
data class Feat135AnalyticsEvent9(val name: String, val value: String)
data class Feat135AnalyticsEvent10(val name: String, val value: String)

fun logFeat135Event1(event: Feat135AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat135Event2(event: Feat135AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat135Event3(event: Feat135AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat135Event4(event: Feat135AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat135Event5(event: Feat135AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat135Event6(event: Feat135AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat135Event7(event: Feat135AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat135Event8(event: Feat135AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat135Event9(event: Feat135AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat135Event10(event: Feat135AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat135Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat135Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat135Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat135Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat135Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat135Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat135Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat135Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat135Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat135Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat135(u: CoreUser): Feat135Projection1 =
    Feat135Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat135Projection1> {
    val list = java.util.ArrayList<Feat135Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat135(u)
    }
    return list
}
