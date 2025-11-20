package com.romix.feature.feat197

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat197Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat197UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat197FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat197UserSummary
)

data class Feat197UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat197NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat197Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat197Config = Feat197Config()
) {

    fun loadSnapshot(userId: Long): Feat197NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat197NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat197UserSummary {
        return Feat197UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat197FeedItem> {
        val result = java.util.ArrayList<Feat197FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat197FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat197UiMapper {

    fun mapToUi(model: List<Feat197FeedItem>): Feat197UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat197UiModel(
            header = UiText("Feat197 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat197UiModel =
        Feat197UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat197UiModel =
        Feat197UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat197UiModel =
        Feat197UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat197Service(
    private val repository: Feat197Repository,
    private val uiMapper: Feat197UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat197UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat197UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat197UserItem1(val user: CoreUser, val label: String)
data class Feat197UserItem2(val user: CoreUser, val label: String)
data class Feat197UserItem3(val user: CoreUser, val label: String)
data class Feat197UserItem4(val user: CoreUser, val label: String)
data class Feat197UserItem5(val user: CoreUser, val label: String)
data class Feat197UserItem6(val user: CoreUser, val label: String)
data class Feat197UserItem7(val user: CoreUser, val label: String)
data class Feat197UserItem8(val user: CoreUser, val label: String)
data class Feat197UserItem9(val user: CoreUser, val label: String)
data class Feat197UserItem10(val user: CoreUser, val label: String)

data class Feat197StateBlock1(val state: Feat197UiModel, val checksum: Int)
data class Feat197StateBlock2(val state: Feat197UiModel, val checksum: Int)
data class Feat197StateBlock3(val state: Feat197UiModel, val checksum: Int)
data class Feat197StateBlock4(val state: Feat197UiModel, val checksum: Int)
data class Feat197StateBlock5(val state: Feat197UiModel, val checksum: Int)
data class Feat197StateBlock6(val state: Feat197UiModel, val checksum: Int)
data class Feat197StateBlock7(val state: Feat197UiModel, val checksum: Int)
data class Feat197StateBlock8(val state: Feat197UiModel, val checksum: Int)
data class Feat197StateBlock9(val state: Feat197UiModel, val checksum: Int)
data class Feat197StateBlock10(val state: Feat197UiModel, val checksum: Int)

fun buildFeat197UserItem(user: CoreUser, index: Int): Feat197UserItem1 {
    return Feat197UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat197StateBlock(model: Feat197UiModel): Feat197StateBlock1 {
    return Feat197StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat197UserSummary> {
    val list = java.util.ArrayList<Feat197UserSummary>(users.size)
    for (user in users) {
        list += Feat197UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat197UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat197UiModel {
    val summaries = (0 until count).map {
        Feat197UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat197UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat197UiModel> {
    val models = java.util.ArrayList<Feat197UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat197AnalyticsEvent1(val name: String, val value: String)
data class Feat197AnalyticsEvent2(val name: String, val value: String)
data class Feat197AnalyticsEvent3(val name: String, val value: String)
data class Feat197AnalyticsEvent4(val name: String, val value: String)
data class Feat197AnalyticsEvent5(val name: String, val value: String)
data class Feat197AnalyticsEvent6(val name: String, val value: String)
data class Feat197AnalyticsEvent7(val name: String, val value: String)
data class Feat197AnalyticsEvent8(val name: String, val value: String)
data class Feat197AnalyticsEvent9(val name: String, val value: String)
data class Feat197AnalyticsEvent10(val name: String, val value: String)

fun logFeat197Event1(event: Feat197AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat197Event2(event: Feat197AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat197Event3(event: Feat197AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat197Event4(event: Feat197AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat197Event5(event: Feat197AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat197Event6(event: Feat197AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat197Event7(event: Feat197AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat197Event8(event: Feat197AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat197Event9(event: Feat197AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat197Event10(event: Feat197AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat197Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat197Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat197Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat197Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat197Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat197Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat197Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat197Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat197Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat197Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat197(u: CoreUser): Feat197Projection1 =
    Feat197Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat197Projection1> {
    val list = java.util.ArrayList<Feat197Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat197(u)
    }
    return list
}
