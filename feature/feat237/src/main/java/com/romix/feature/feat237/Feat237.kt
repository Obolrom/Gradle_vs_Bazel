package com.romix.feature.feat237

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat237Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat237UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat237FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat237UserSummary
)

data class Feat237UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat237NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat237Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat237Config = Feat237Config()
) {

    fun loadSnapshot(userId: Long): Feat237NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat237NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat237UserSummary {
        return Feat237UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat237FeedItem> {
        val result = java.util.ArrayList<Feat237FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat237FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat237UiMapper {

    fun mapToUi(model: List<Feat237FeedItem>): Feat237UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat237UiModel(
            header = UiText("Feat237 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat237UiModel =
        Feat237UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat237UiModel =
        Feat237UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat237UiModel =
        Feat237UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat237Service(
    private val repository: Feat237Repository,
    private val uiMapper: Feat237UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat237UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat237UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat237UserItem1(val user: CoreUser, val label: String)
data class Feat237UserItem2(val user: CoreUser, val label: String)
data class Feat237UserItem3(val user: CoreUser, val label: String)
data class Feat237UserItem4(val user: CoreUser, val label: String)
data class Feat237UserItem5(val user: CoreUser, val label: String)
data class Feat237UserItem6(val user: CoreUser, val label: String)
data class Feat237UserItem7(val user: CoreUser, val label: String)
data class Feat237UserItem8(val user: CoreUser, val label: String)
data class Feat237UserItem9(val user: CoreUser, val label: String)
data class Feat237UserItem10(val user: CoreUser, val label: String)

data class Feat237StateBlock1(val state: Feat237UiModel, val checksum: Int)
data class Feat237StateBlock2(val state: Feat237UiModel, val checksum: Int)
data class Feat237StateBlock3(val state: Feat237UiModel, val checksum: Int)
data class Feat237StateBlock4(val state: Feat237UiModel, val checksum: Int)
data class Feat237StateBlock5(val state: Feat237UiModel, val checksum: Int)
data class Feat237StateBlock6(val state: Feat237UiModel, val checksum: Int)
data class Feat237StateBlock7(val state: Feat237UiModel, val checksum: Int)
data class Feat237StateBlock8(val state: Feat237UiModel, val checksum: Int)
data class Feat237StateBlock9(val state: Feat237UiModel, val checksum: Int)
data class Feat237StateBlock10(val state: Feat237UiModel, val checksum: Int)

fun buildFeat237UserItem(user: CoreUser, index: Int): Feat237UserItem1 {
    return Feat237UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat237StateBlock(model: Feat237UiModel): Feat237StateBlock1 {
    return Feat237StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat237UserSummary> {
    val list = java.util.ArrayList<Feat237UserSummary>(users.size)
    for (user in users) {
        list += Feat237UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat237UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat237UiModel {
    val summaries = (0 until count).map {
        Feat237UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat237UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat237UiModel> {
    val models = java.util.ArrayList<Feat237UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat237AnalyticsEvent1(val name: String, val value: String)
data class Feat237AnalyticsEvent2(val name: String, val value: String)
data class Feat237AnalyticsEvent3(val name: String, val value: String)
data class Feat237AnalyticsEvent4(val name: String, val value: String)
data class Feat237AnalyticsEvent5(val name: String, val value: String)
data class Feat237AnalyticsEvent6(val name: String, val value: String)
data class Feat237AnalyticsEvent7(val name: String, val value: String)
data class Feat237AnalyticsEvent8(val name: String, val value: String)
data class Feat237AnalyticsEvent9(val name: String, val value: String)
data class Feat237AnalyticsEvent10(val name: String, val value: String)

fun logFeat237Event1(event: Feat237AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat237Event2(event: Feat237AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat237Event3(event: Feat237AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat237Event4(event: Feat237AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat237Event5(event: Feat237AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat237Event6(event: Feat237AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat237Event7(event: Feat237AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat237Event8(event: Feat237AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat237Event9(event: Feat237AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat237Event10(event: Feat237AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat237Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat237Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat237Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat237Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat237Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat237Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat237Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat237Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat237Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat237Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat237(u: CoreUser): Feat237Projection1 =
    Feat237Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat237Projection1> {
    val list = java.util.ArrayList<Feat237Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat237(u)
    }
    return list
}
